package org.hrds.rdupm.init.service;

/**
 * description
 *
 * @author chenxiuhong 2020/05/28 2:37 下午
 */
public interface HarborInitService {
	void defaultRepoInit();

	void customRepoInit();

	void initHarborCustomRepoNoAnyId();

	void fixHarborUserAuth();
}
