package org.hrds.rdupm.nexus.infra.util;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public class PageConvertUtils {
    /**
     * hzero分页转换为猪齿鱼分页
     *
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> convert(Page<T> page) {
        com.github.pagehelper.Page<T> c7nPage = new com.github.pagehelper.Page<>(page.getNumber() + 1, page.getSize());
        c7nPage.addAll(page.getContent());
        c7nPage.setTotal(page.getTotalElements());

        PageInfo<T> pageInfo = new PageInfo<>(c7nPage);
        pageInfo.setTotal(page.getTotalElements());

        return pageInfo;
    }

    /**
     * 无数据转换为猪齿鱼分页
     * @return PageInfo
     */
    public static <T> PageInfo<T> convert() {
        com.github.pagehelper.Page<T> c7nPage = new com.github.pagehelper.Page<>(0, 0);
        c7nPage.addAll(new ArrayList<T>());
        c7nPage.setTotal(0);

        PageInfo<T> pageInfo = new PageInfo<>(c7nPage);
        pageInfo.setTotal(0);

        return pageInfo;
    }

    /**
     * 所有数据转换为猪齿鱼分页
     * @return PageInfo
     */
    public static <T> PageInfo<T> convert(int pageNum, int pageSize, List<T> allContent) {
        com.github.pagehelper.Page<T> page = new com.github.pagehelper.Page<>(pageNum, pageSize);
        int currentNum = (pageNum - 1) * pageSize;
        int total = allContent.size();
        page.addAll(allContent.stream().skip(currentNum).limit(pageSize).collect(Collectors.toList()));
        page.setTotal(total);

        PageInfo<T> pageInfo = new PageInfo<>(page);
        pageInfo.setTotal(total);

        return pageInfo;
    }
}
