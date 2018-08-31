package com.marklogic.junit.dhf;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Extends AbstractSpringTest and uses DataHubTestConfig, which provides a sensible default configuration for connecting
 * to a DHF application. Also provides a runHarmonizeFlow convenience method.
 * <p>
 * You do not need to use this class - most of the interesting functionality is delegated to the TestFlowRunner class.
 */
@ContextConfiguration(classes = {DataHubTestConfig.class})
public abstract class AbstractDataHubTest extends AbstractSpringTest {

	@Autowired
	protected DataHubTestConfig dataHubTestConfig;

	protected DatabaseClient stagingClient;

	@Before
	public void setupStagingClient() {
		stagingClient = DatabaseClientFactory.newClient(
			dataHubTestConfig.getHost(), dataHubTestConfig.getStagingPort(), dataHubTestConfig.getStagingDatabaseName(),
			new DatabaseClientFactory.DigestAuthContext(dataHubTestConfig.getUsername(), dataHubTestConfig.getPassword())
		);
	}

	@After
	public void releaseStagingClient() {
		if (stagingClient != null) {
			stagingClient.release();
		}
	}

	/**
	 * Runs a flow with the stagingClient being used as the "source" client.
	 *
	 * @param entityName
	 * @param flowName
	 * @param optionKeysAndValues
	 * @return
	 */
	protected JobTicket runHarmonizeFlow(String entityName, String flowName, String... optionKeysAndValues) {
		TestFlowRunner testFlowRunner = new TestFlowRunner(this.stagingClient, dataHubTestConfig.getTestDatabaseName());
		testFlowRunner.setJobsClient(newJobsDatabaseClient());
		return testFlowRunner.runHarmonizeFlow(entityName, flowName, optionKeysAndValues);
	}

	/**
	 * Assumes digest auth - can override this via a subclass.
	 *
	 * @return
	 */
	protected DatabaseClient newJobsDatabaseClient() {
		return DatabaseClientFactory.newClient(
			dataHubTestConfig.getHost(), dataHubTestConfig.getJobPort(),
			new DatabaseClientFactory.DigestAuthContext(dataHubTestConfig.getUsername(), dataHubTestConfig.getPassword())
		);
	}
}
