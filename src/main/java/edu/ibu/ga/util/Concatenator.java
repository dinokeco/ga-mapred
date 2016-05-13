package edu.ibu.ga.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Concatenator extends Configured implements Tool {

	private static final Logger LOGGER = Logger.getLogger(Concatenator.class);
	private static final String HTTP_PROTOCOL_PREFIX = "http://";
	private static final String DEFAULT_TASK_TRACKER_PORT = "50060";
	private static final String USER_LOG_RESOURCE = "/logs/userlogs/";
	private static final String SYS_LOG_RESOURCE = "/syslog";
	private static final String HADOOP_JOB_ID = "hadoop.job.id";
	private static final String OUTPUT_LOG_FILE = "output.log.file";
	private static final String TASK_TRACKER_PORT = "hadoop.mapreduce.tasktracker.port";
	private static final String COMPRESS_OUTPUT = "compress.output";

	public Concatenator() {
	}

	private void usage() {
		LOGGER.info("Usage > hadoop jar <jar-file> -Dhadoop.job.id=<job-id> -Doutput.log.file=<output-file> [ -Dcompress.output=<true|false default false> -Dhadoop.mapreduce.tasktracker.port=<port default 50060>");
		System.exit(0);
	}

	private void validateConfiguration(Configuration arguments, Configuration conf) {
		if (arguments.get(HADOOP_JOB_ID) == null) {
			LOGGER.error("Missing hadoop.job.id property in configuration");
			usage();
		}
		conf.set(HADOOP_JOB_ID, arguments.get(HADOOP_JOB_ID));
		if (arguments.get(OUTPUT_LOG_FILE) == null) {
			usage();
		}
		conf.set(OUTPUT_LOG_FILE, arguments.get(OUTPUT_LOG_FILE));
	}

	public static void main(String args[]) throws Exception {
		ToolRunner.run(new Concatenator(), args);
	}

	public int run(String arg0[]) throws Exception {
		long startTimeSt = System.currentTimeMillis();
		Configuration conf = new Configuration();
		validateConfiguration(getConf(), conf);
		String jobId = conf.get(HADOOP_JOB_ID);
		String taskTrackerPort = conf.get(TASK_TRACKER_PORT, DEFAULT_TASK_TRACKER_PORT);
		String outputFile = conf.get(OUTPUT_LOG_FILE);
		Boolean compressOutput = Boolean.valueOf(conf.getBoolean(COMPRESS_OUTPUT, false));
		InetSocketAddress jobtracker = new InetSocketAddress("localhost", 9001);
		//conf.set("hbase.zookeeper.quorum", "dchilcmsdn01.hq.navteq.com,dchilcmsdn02.hq.navteq.com,dchilcmsdn03.hq.navteq.com");
		//conf.set("hbase.zookeeper.property.clientPort", "2181");
		// JobClient client = new JobClient(new JobConf(conf));
		JobClient client = new JobClient(jobtracker, conf);
		client.setConf(conf);

		List taskTrackerNodes = getTaskTrackerNodes(client);
		OutputStream outputStream;
		if (compressOutput.booleanValue())
			outputStream = new BZip2CompressorOutputStream(new FileOutputStream(outputFile));
		else
			outputStream = new FileOutputStream(outputFile);
		for (Iterator iterator = taskTrackerNodes.iterator(); iterator.hasNext();) {
			String taskTrackerNode = (String) iterator.next();
			String jobLogURL = HTTP_PROTOCOL_PREFIX.concat(taskTrackerNode).concat(":").concat(taskTrackerPort).concat(USER_LOG_RESOURCE).concat(jobId);
			Iterator it = getTaskAttemptsForNode(jobLogURL).iterator();
			while (it.hasNext()) {
				String taskAttempt = (String) iterator.next();
				String taskAttemptLogURL = jobLogURL.concat("/").concat(taskAttempt).concat(SYS_LOG_RESOURCE);
				LOGGER.info((new StringBuilder()).append("Downloading file ").append(taskAttemptLogURL).toString());
				URL url = new URL(taskAttemptLogURL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(2);
				connection.setReadTimeout(2);
				InputStream logInputStream = url.openStream();
				IOUtils.copy(logInputStream, outputStream);
				logInputStream.close();
				outputStream.flush();
			}
		}

		outputStream.close();
		LOGGER.info((new StringBuilder()).append("Download complete in ").append(System.currentTimeMillis() - startTimeSt).append("ms").toString());
		return 0;
	}

	private List getTaskTrackerNodes(JobClient client) throws IOException {
		ClusterStatus status = client.getClusterStatus(true);
		List taskTrackerNodes = new ArrayList(status.getActiveTrackerNames().size());
		String a;
		for (Iterator i$ = status.getActiveTrackerNames().iterator(); i$.hasNext(); taskTrackerNodes.add(a.replace("tracker_", "").split(":")[0]))
			a = (String) i$.next();

		return taskTrackerNodes;
	}

	// STring list I sink
	public List getTaskAttemptsForNode(String surl) throws IOException {
		URL url = new URL(surl);
		List attemptList = new ArrayList();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			do {
				String inputLine;
				if ((inputLine = in.readLine()) == null)
					break;
				Pattern p = Pattern.compile("(attempt_\\d+_\\d+_[m|r]_\\d+_\\d+)");
				Matcher m = p.matcher(inputLine);
				if (m.find())
					attemptList.add(m.group(1));
			} while (true);
			in.close();
		} catch (IOException e) {
			LOGGER.debug((new StringBuilder()).append("No data at URL ").append(url).toString());
		}
		return attemptList;
	}

	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classnotfoundexception) {
			throw new NoClassDefFoundError(classnotfoundexception.getMessage());
		}
	}
}
