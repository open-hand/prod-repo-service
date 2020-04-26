package org.hrds.rdupm.harbor.infra.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
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

		String expires = String.valueOf(new Double(doubleExpire).intValue()) + "000";
		Date endDate = new Date(Long.parseLong(expires));
		return endDate;
	}

	public static long dateToTimestamp(Date date){
		String timestamp = String.valueOf(date.getTime());
		String str = timestamp.substring(0,timestamp.length()-3);
		return new Long(str);
	}

	public static void main(String[] args){
		String config =  "{\"architecture\":\"amd64\",\"config\":{\"Hostname\":\"\",\"Domainname\":\"\",\"User\":\"nginx\",\"AttachStdin\":false,\"AttachStdout\":false,\"AttachStderr\":false,\"ExposedPorts\":{\"8080/tcp\":{}},\"Tty\":false,\"OpenStdin\":false,\"StdinOnce\":false,\"Env\":[\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"],\"Cmd\":[\"nginx\",\"-g\",\"daemon off;\"],\"Healthcheck\":{\"Test\":[\"CMD-SHELL\",\"curl --fail -s http://127.0.0.1:8080 || exit 1\"]},\"ArgsEscaped\":true,\"Image\":\"sha256:0946edf203d07013e83d1098e1249715d667d9704f4690be58269ee518b678ef\",\"Volumes\":{\"/run\":{},\"/var/cache/nginx\":{},\"/var/log/nginx\":{}},\"WorkingDir\":\"\",\"Entrypoint\":null,\"OnBuild\":null,\"Labels\":{\"build-date\":\"20191025\",\"name\":\"Photon OS 2.0 Base Image\",\"vendor\":\"VMware\"},\"StopSignal\":\"SIGQUIT\"},\"container\":\"3394fa402460946aea2d1803c075cc074ad15aa880e2c379f65e953010e06666\",\"container_config\":{\"Hostname\":\"3394fa402460\",\"Domainname\":\"\",\"User\":\"nginx\",\"AttachStdin\":false,\"AttachStdout\":false,\"AttachStderr\":false,\"ExposedPorts\":{\"8080/tcp\":{}},\"Tty\":false,\"OpenStdin\":false,\"StdinOnce\":false,\"Env\":[\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"],\"Cmd\":[\"/bin/sh\",\"-c\",\"#(nop) \",\"CMD [\\\"nginx\\\" \\\"-g\\\" \\\"daemon off;\\\"]\"],\"Healthcheck\":{\"Test\":[\"CMD-SHELL\",\"curl --fail -s http://127.0.0.1:8080 || exit 1\"]},\"ArgsEscaped\":true,\"Image\":\"sha256:0946edf203d07013e83d1098e1249715d667d9704f4690be58269ee518b678ef\",\"Volumes\":{\"/run\":{},\"/var/cache/nginx\":{},\"/var/log/nginx\":{}},\"WorkingDir\":\"\",\"Entrypoint\":null,\"OnBuild\":null,\"Labels\":{\"build-date\":\"20191025\",\"name\":\"Photon OS 2.0 Base Image\",\"vendor\":\"VMware\"},\"StopSignal\":\"SIGQUIT\"},\"created\":\"2020-02-10T06:12:53.380147749Z\",\"docker_version\":\"18.06.3-ce\",\"history\":[{\"created\":\"2019-10-28T21:26:10.234058084Z\",\"created_by\":\"/bin/sh -c #(nop) ADD file:383fe94c8068ad68dc54778061b329b5d136d9d9c8911f2605271e2f82eb7e1a in / \"},{\"created\":\"2019-10-28T21:26:10.399504887Z\",\"created_by\":\"/bin/sh -c #(nop)  LABEL name=Photon OS 2.0 Base Image vendor=VMware build-date=20191025\",\"empty_layer\":true},{\"created\":\"2019-10-28T21:26:10.562853047Z\",\"created_by\":\"/bin/sh -c #(nop)  CMD [\\\"/bin/bash\\\"]\",\"empty_layer\":true},{\"created\":\"2019-11-11T08:41:28.435852912Z\",\"created_by\":\"/bin/sh -c tdnf install -y nginx sudo \\u003e\\u003e /dev/null     \\u0026\\u0026 ln -sf /dev/stdout /var/log/nginx/access.log     \\u0026\\u0026 ln -sf /dev/stderr /var/log/nginx/error.log     \\u0026\\u0026 groupadd -r -g 10000 nginx \\u0026\\u0026 useradd --no-log-init -r -g 10000 -u 10000 nginx     \\u0026\\u0026 chown -R nginx:nginx /etc/nginx     \\u0026\\u0026 tdnf clean all\"},{\"created\":\"2020-02-10T06:12:51.277575018Z\",\"created_by\":\"/bin/sh -c #(nop) COPY dir:c6f0563b977689ced138b3f0a99d66a814051d8bdef55bc0eea601784437cc01 in /usr/share/nginx/html \"},{\"created\":\"2020-02-10T06:12:51.537413511Z\",\"created_by\":\"/bin/sh -c #(nop) COPY file:64a65ad1cf4dd6de2c18b9672b3bb3a3dd6e1020aaf8906020eac6cfcbdefd3b in /usr/share/nginx/html \"},{\"created\":\"2020-02-10T06:12:51.785343133Z\",\"created_by\":\"/bin/sh -c #(nop) COPY file:a002b6e89014247560ea7decc609952f28da2e8d4527832ea7f5d87e6f18efda in /usr/share/nginx/html \"},{\"created\":\"2020-02-10T06:12:52.046087716Z\",\"created_by\":\"/bin/sh -c #(nop) COPY file:03d32f9cd62e06eb9adda333311ffbd3f2e42f7b25a7acab1957d19f5f556219 in /usr/share/nginx/html \"},{\"created\":\"2020-02-10T06:12:52.316517687Z\",\"created_by\":\"/bin/sh -c #(nop) COPY file:7b00af0a1f5e0e02bab7a03a0357e556577cfcc376a63297e9e4c2aabdb10aca in /etc/nginx/nginx.conf \"},{\"created\":\"2020-02-10T06:12:52.477162709Z\",\"created_by\":\"/bin/sh -c #(nop)  EXPOSE 8080\",\"empty_layer\":true},{\"created\":\"2020-02-10T06:12:52.660296586Z\",\"created_by\":\"/bin/sh -c #(nop)  VOLUME [/var/cache/nginx /var/log/nginx /run]\",\"empty_layer\":true},{\"created\":\"2020-02-10T06:12:52.840211798Z\",\"created_by\":\"/bin/sh -c #(nop)  STOPSIGNAL SIGQUIT\",\"empty_layer\":true},{\"created\":\"2020-02-10T06:12:53.027919351Z\",\"created_by\":\"/bin/sh -c #(nop)  HEALTHCHECK \\u0026{[\\\"CMD-SHELL\\\" \\\"curl --fail -s http://127.0.0.1:8080 || exit 1\\\"] \\\"0s\\\" \\\"0s\\\" \\\"0s\\\" '\\\\x00'}\",\"empty_layer\":true},{\"created\":\"2020-02-10T06:12:53.208097595Z\",\"created_by\":\"/bin/sh -c #(nop)  USER nginx\",\"empty_layer\":true},{\"created\":\"2020-02-10T06:12:53.380147749Z\",\"created_by\":\"/bin/sh -c #(nop)  CMD [\\\"nginx\\\" \\\"-g\\\" \\\"daemon off;\\\"]\",\"empty_layer\":true}],\"os\":\"linux\",\"rootfs\":{\"type\":\"layers\",\"diff_ids\":[\"sha256:47a4bb1cfbc75e1073f8b6fc0806588bbde1142cae1ff0be70d4a111aaa05b0d\",\"sha256:9ea2dad46741aa1ed38b875d8283ce840523a0b308ccadea5d5da574b28b0575\",\"sha256:b280b9e7ca3ba6c977a0bcb9247c0164b8b4406b8eab362d47eea0fb0fc4ed04\",\"sha256:a1ce7c1130eef916fea987787cedf13b5bd7ade72910b6bb00e1864e5b45e191\",\"sha256:3b0492a7358bb2322d89fb43835662ffae2ea07904c0d84e78edb4f1cce3d10f\",\"sha256:c64d6e6c80dd9d07ca880566aea040079347a7723645adfa47c856d7c12da765\",\"sha256:ace4e9064f062ca8c644ec4c971c9cca0b5af1a6400a25a658a6cd205da5b9fc\"]}}";
		Map<String,Object> configMap = new Gson().fromJson(config,Map.class);
		List<Map<String,Object>> historyList = (List<Map<String, Object>>) configMap.get("history");
		for(Map<String,Object> history : historyList){
			System.out.println(history.get("created") + "  " + history.get("created_by"));
		}
		System.out.println(111);
	}

}
