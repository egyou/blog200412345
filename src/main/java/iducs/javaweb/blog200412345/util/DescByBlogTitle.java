package iducs.javaweb.blog200412345.util;

import iducs.javaweb.blog200412345.model.Blog;

import java.util.Comparator;

public class DescByBlogTitle implements Comparator<Blog> {
    @Override
    public int compare(Blog o1, Blog o2) { return o2.getTitle().compareTo(o1.getTitle()); } // o2 <= o1 : 음수 / o2 > o1 : 1이상 내림차순
    //오름차순 -> public int compare(Blog o1, Blog o2) { return o1.getTitle().compareTo(o2.getTitle()); }
}
