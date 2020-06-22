package org.hrds.rdupm.harbor.infra.util;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
			case HarborConstants.B: storageLimit = new BigDecimal(storageNum).longValue();break;
			case HarborConstants.KB: storageLimit = new BigDecimal(storageNum).multiply((new BigDecimal(1024).pow(1))).longValue();break;
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

	/**
	 * 将自动生成的一些字段值清空
	 *
	 * @param auditDomain 对象
	 */
	public static void resetDomain(AuditDomain auditDomain) {
		Class c = auditDomain.getClass();
		String idFieldName = Arrays.stream(c.getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(Id.class))
				.collect(Collectors.toList()).get(0).getName();
		try {
			FieldUtils.writeDeclaredField(auditDomain, idFieldName, null, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		auditDomain.setCreatedBy(null);
		auditDomain.setCreationDate(null);
		auditDomain.setObjectVersionNumber(null);
		auditDomain.setLastUpdateDate(null);
		auditDomain.setLastUpdatedBy(null);
	}


	public static String castToSearchParam(Map<String, Object> params) {
		Map<String, Map<String, Object>> mapParams = new HashMap<>(16);
		mapParams.put("searchParam", params);
		return new Gson().toJson(mapParams);
	}


	/**
	 * 随机生成长度为len的密码，且包括大写、小写英文字母和数字
	 * @author xuhui
	 */

	static char[] bigNum = new char[26];
	static char[] smallNum = new char[26];
	static int[] num = new int[10];
	public static String getPassword(){
		int len = 8;
		String str = "";
		init();
		Random random = new Random();
		//需要先随机生成len长度中，大写字母的个数，小写字母的个数以及数字的个数，且保证每个个数都不能为0
		int big_len = random.nextInt(len-2)+1;
		int small_len = random.nextInt(len-big_len-1)+1;
		int num_len = len-big_len-small_len;
		//每一位生成对应的密码
		for(int i=0;i<big_len;i++){
			str += bigNum[random.nextInt(26)];
		}
		for(int i=0;i<small_len;i++){
			str += smallNum[random.nextInt(26)];
		}
		for(int i=0;i<num_len;i++){
			str += num[random.nextInt(10)];
		}
		return str;
	}

	public static void init(){
		//生成大写字母表,对照ASIC表
		for(int i=65;i<=90;i++){
			bigNum[i-65]=(char) i;
		}
		//生成小写字母表
		for(int i=97;i<=122;i++){
			smallNum[i-97]=(char) i;
		}
		//生成数字表
		for(int i=0;i<=9;i++){
			num[i]=i;
		}
	}
}
