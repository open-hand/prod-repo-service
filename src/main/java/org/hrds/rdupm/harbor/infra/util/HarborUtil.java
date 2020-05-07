package org.hrds.rdupm.harbor.infra.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.api.vo.HarborProjectVo;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.export.vo.ExportParam;

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

	public static Map<String,Object> getStorageNumUnit(Integer size) {
		//如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return storageMap(size,"B");
		} else {
			size = size / 1024;
		}

		//如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		if (size < 1024) {
			return storageMap(size,"KB");
		} else {
			size = size / 1024;
		}

		if (size < 1024) {
			return storageMap(size,"MB");
		} else {
			size = size / 1024;
		}

		if (size < 1024) {
			return storageMap(size,"GB");
		} else {
			size = size / 1024;
		}

		return storageMap(size,"TB");
	}

	public static Map<String,Object> storageMap(Object storageNum,Object storageUnit){
		Map<String,Object> map = new HashMap<>();
		map.put("storageNum",storageNum);
		map.put("storageUnit",storageUnit);
		return map;
	}

	public static String getTagSizeDesc(Integer size) {
		Map<String,Object> sizeMap = getStorageNumUnit(size);
		Integer storageNum = (Integer) sizeMap.get("storageNum");
		String storageUnit = (String) sizeMap.get("storageUnit");
		return storageNum+storageUnit;
	}

	public static Date timestampToDate(Map<String,Object> whiteMap){
		if(whiteMap == null){
			return null;
		}

		Double doubleExpire = (Double) whiteMap.get("expires_at");
		if(doubleExpire == null){
			return null;
		}

		String expires = String.valueOf(new Double(doubleExpire).intValue()*1000);
		Date endDate = new Date(Long.parseLong(expires));
		return endDate;
	}

	public static Long dateToTimestamp(Date date){
		if(date == null){
			return null;
		}
		return date.getTime()/1000;
	}

	/***
	 * 设置导出全部列
	 * @param exportParam
	 */
	public static void setIds(ExportParam exportParam){
		//无需在前台指定"列ids"
		Set<Long> ids = new HashSet<>(16);
		exportParam.setIds(ids);
		int fieldLength = HarborAuth.class.getDeclaredFields().length;
		for(int i=1;i<=fieldLength+1;i++){
			ids.add((long)i);
		}
	}

	/***
	 * 校验字符串是否在值区间内
	 * @param str 字符串
	 * @param fieldName 字段名
	 * @param errorMsgCode 消息code
	 * @param args 参数
	 */
	public static void notIn(String str,String fieldName,String errorMsgCode,String... args){
		if(StringUtils.isEmpty(str)){
			return;
		}
		boolean flag = false;

		int length = args.length;
		for(int i=0; i < length; i++){
			if(str.equals(args[i])){
				flag = true;
				break;
			}
		}

		if(!flag){
			throw new CommonException(errorMsgCode,fieldName,str);
		}
	}

}
