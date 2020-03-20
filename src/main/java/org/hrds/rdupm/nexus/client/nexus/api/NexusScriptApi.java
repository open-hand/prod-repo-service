package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusScript;

/**
 * nexus 脚本相关api
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public interface NexusScriptApi {

	/**
	 * 脚本上传
	 * @param nexusScript 脚本信息
	 */
	void uploadScript(NexusScript nexusScript);

	/**
	 * 脚本更新
	 * @param scriptName 脚本名称
	 * @param nexusScript 脚本信息
	 */
	void updateScript(String scriptName, NexusScript nexusScript);

	/**
	 * 脚本执行
	 * @param scriptName 脚本名称
	 * @param param 参数信息
	 */
	void runScript(String scriptName, String param);

	/**
	 * 脚本删除
	 * @param scriptName 脚本名称
	 */
	void deleteScript(String scriptName);
}
