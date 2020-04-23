package org.hrds.rdupm.harbor.infra.util;

import java.text.SimpleDateFormat;
import java.util.Date;
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
		if(storageNum == -1){
			return storageLimit;
		}
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
		String storageUnit = HarborConstants.KB;
		if(storageLimt > 0){
			Integer divisionResult1 = Double.valueOf(storageLimt/(1024)).intValue();
			Integer divisionResult2 = Double.valueOf(storageLimt/(1024*1024)).intValue();
			Integer divisionResult3 = Double.valueOf(storageLimt/(1024*1024*1024)).intValue();
			Integer divisionResult4 = Double.valueOf(storageLimt/(1024*1024*1024)).intValue();
			if(divisionResult1 >= 1){
				storageNum = divisionResult1;
				storageUnit = HarborConstants.KB;
			}
			if(divisionResult2 >= 1){
				storageNum = divisionResult2;
				storageUnit = HarborConstants.MB;
			}
			if(divisionResult3 >= 1){
				storageNum = divisionResult3;
				storageUnit = HarborConstants.GB;
			}
			if(divisionResult4 >= 1){
				storageNum = divisionResult4;
				storageUnit = HarborConstants.TB;
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("storageNum",storageNum);
		map.put("storageUnit",storageUnit);
		return map;
	}

	public static Date timestampToDate(Map<String,Object> whiteMap){
		if(whiteMap == null){
			return null;
		}

		Double doubleExpire = (Double) whiteMap.get("expires_at");
		if(doubleExpire == null){
			return null;
		}

		String expires = String.valueOf(new Double(doubleExpire).intValue()) + "000";
		Date endDate = new Date(Long.parseLong(expires));
		return endDate;
	}

	public static long dateToTimestamp(Date date){
		String timestamp = String.valueOf(date.getTime());
		String str = timestamp.substring(0,timestamp.length()-3);
		return new Long(str);
	}

}
