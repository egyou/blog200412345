package iducs.javaweb.blog200412345.util;

import iducs.javaweb.blog200412345.model.Blog;

import java.util.Comparator;

public class AscByBlogEmail implements Comparator<Blog> {
    @Override
    public int compare(Blog o1, Blog o2) {
        return o1.getEmail().compareTo(o2.getEmail());
    }
    //이메일 기준 오름차순
}
