package org.hrds.rdupm.nexus.client.nexus.api;

import org.hrds.rdupm.nexus.client.nexus.model.NexusScriptResult;
import org.hrds.rdupm.nexus.client.nexus.model.NexusServerScript;

/**
 * nexus 脚本相关api
 * @author weisen.yang@hand-china.com 2020/3/20
 */
public interface NexusScriptApi {

	/**
	 * 脚本查询
	 * @param scriptName 脚本名称
	 * @return NexusServerScript
	 */
	NexusServerScript getScript(String scriptName);

	/**
	 * 脚本上传
	 * @param nexusScript 脚本信息
	 */
	void uploadScript(NexusServerScript nexusScript);

	/**
	 * 脚本更新
	 * @param scriptName 脚本名称
	 * @param nexusScript 脚本信息
	 */
	void updateScript(String scriptName, NexusServerScript nexusScript);

	/**
	 * 脚本执行
	 * @param scriptName 脚本名称
	 * @param param 参数信息
	 * @return  NexusScriptResult NexusScriptResult
	 */
	NexusScriptResult runScript(String scriptName, String param);

	/**
	 * 脚本删除
	 * @param scriptName 脚本名称
	 */
	void deleteScript(String scriptName);

	/**
	 * script脚本初始化: 根据 NexusApiConstants.ScriptName.SCRIPT_LIST 的数据从 resource/script/nexus下处理文件
	 */
	void initScript();
}
