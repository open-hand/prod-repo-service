package org.hrds.rdupm.harbor.infra.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rdupm.harbor.domain.entity.HarborAuth;
import org.hrds.rdupm.harbor.infra.constant.HarborConstants;
import org.hzero.export.vo.ExportParam;

/**
 * description
 *
 * @author chenxiuhong 2020/04/22 5:13 下午
 */
public class HarborUtil {

	public static Long getStorageLimit(Integer storageNum,String storageUnit){
		Long storageLimit = -1L;
		if(storageNum == -1){
			return storageLimit;
		}
		switch (storageUnit){
			case HarborConstants.MB: storageLimit = new BigDecimal(storageNum).multiply((new BigDecimal(1024).pow(2))).longValue();break;
			case HarborConstants.GB: storageLimit = new BigDecimal(storageNum).multiply((new BigDecimal(1024).pow(3))).longValue();break;
			case HarborConstants.TB: storageLimit = new BigDecimal(storageNum).multiply((new BigDecimal(1024).pow(4))).longValue();break;
			default: break;
		}
		return storageLimit;
	}

	/***
	 * 获得整数
	 * @param size
	 * @return
	 */
	public static Map<String,Object> getStorageNumUnit(Long size) {
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

	/***
	 * 保存两位小数
	 * @param num
	 * @return
	 */
	public static Map<String,Object> getUsedStorageNumUnit(Long num) {
		BigDecimal size = new BigDecimal(num);

		//如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size.doubleValue() < 1024) {
			return storageMap(size,"B");
		} else {
			size = size.divide(new BigDecimal(1024),2,BigDecimal.ROUND_HALF_UP);
		}

		//如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		if (size.doubleValue() < 1024) {
			return storageMap(size,"KB");
		} else {
			size = size.divide(new BigDecimal(1024),2,BigDecimal.ROUND_HALF_UP);
		}

		if (size.doubleValue() < 1024) {
			return storageMap(size,"MB");
		} else {
			size = size.divide(new BigDecimal(1024),2,BigDecimal.ROUND_HALF_UP);
		}

		if (size.doubleValue() < 1024) {
			return storageMap(size,"GB");
		} else {
			size = size.divide(new BigDecimal(1024),2,BigDecimal.ROUND_HALF_UP);
		}

		return storageMap(size,"TB");
	}

	public static Map<String,Object> storageMap(Object storageNum,Object storageUnit){
		Map<String,Object> map = new HashMap<>(2);
		map.put("storageNum",storageNum);
		map.put("storageUnit",storageUnit);
		return map;
	}

	public static String getTagSizeDesc(Long size) {
		Map<String,Object> sizeMap = getUsedStorageNumUnit(size);
		BigDecimal storageNum = (BigDecimal) sizeMap.get("storageNum");
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
		String expire = String.valueOf((doubleExpire).longValue());

		Date endDate = new Date(Long.parseLong(expire + "000"));
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

	/***
	 * size转GB、TB等
	 * @param size
	 * @return
	 */
	public static String readableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + "/" + units[digitGroups];
	}

	public static void main(String[] args){
		String expire = "1589596151000";
		String expires = String.valueOf(expire + "000");
		Date endDate = new Date(Long.parseLong(expire));
		System.out.println(readableFileSize(300000230));
	}

}
