package org.hrds.rdupm.harbor.infra.util;

import java.util.HashMap;
import java.util.Map;

import org.hrds.rdupm.harbor.infra.constant.HarborConstants;

/**
 * description
 *
 * @author chenxiuhong 2020/04/22 5:13 下午
 */
public class HarborUtil {

	public static Integer getStorageLimit(Integer storageNum,String storageUnit){
		Integer storageLimit = -1;
		switch (storageUnit){
			case HarborConstants.MB: storageLimit = storageNum*1024*1024;break;
			case HarborConstants.GB: storageLimit = storageNum*1024*1024*1024;break;
			case HarborConstants.TB: storageLimit = storageNum*1024*1024*1024*1024;break;
			default: break;
		}
		return storageLimit;
	}

	public static Map<String,Object> getStorageNumUnit(Integer storageLimt) {
		Integer storageNum = 0;
		String storageUnit = null;

		Integer divisionResult1 = Double.valueOf(storageLimt/(1024*1024)).intValue();
		Integer divisionResult2 = Double.valueOf(storageLimt/(1024*1024*1024)).intValue();
		Integer divisionResult3 = Double.valueOf(storageLimt/(1024*1024*1024)).intValue();
		if(divisionResult1 >= 1){
			storageNum = divisionResult1;
			storageUnit = HarborConstants.MB;
		}
		if(divisionResult2 >= 1){
			storageNum = divisionResult2;
			storageUnit = HarborConstants.GB;
		}
		if(divisionResult3 >= 1){
			storageNum = divisionResult3;
			storageUnit = HarborConstants.TB;
		}

		Map<String,Object> map = new HashMap<>();
		map.put("storageNum",storageNum);
		map.put("storageUnit",storageUnit);
		return map;
	}
}
