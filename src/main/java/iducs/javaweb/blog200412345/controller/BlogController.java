package iducs.javaweb.blog200412345.controller;

import iducs.javaweb.blog200412345.model.Blog;
import iducs.javaweb.blog200412345.model.Member;
import iducs.javaweb.blog200412345.repository.BlogDAOImpl;
import iducs.javaweb.blog200412345.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@WebServlet(name = "post", urlPatterns = { "/blogs/test.do", "/blogs/post-form.do", "/blogs/post.do", "/blogs/list.do", "/blogs/detail.do",
        "/blogs/updateForm.do", "/blogs/update.do", "/blogs/delete.do", "/blogs/sort.do" }) //urlPatterns : 다수의 url 을 기술할 수 있다.
public class BlogController extends HttpServlet {
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String command = uri.substring(contextPath.length() + 1); // blogs/post.do, blog/list.do가 반환됨
        String action = command.substring(command.lastIndexOf("/") + 1); // post.do, list.do 반환

        BlogDAOImpl dao = new BlogDAOImpl();

        if(action.equals("post-form.do")) {

            Member member = new Member();
            member.setEmail(request.getParameter("email"));
            member.setName(request.getParameter("name"));

            request.setAttribute("loginedEmail", member.getEmail()); //email로 조회한 회원 정보 객체를 member라는 키로 request에 attribute로 저장
            request.setAttribute("loginedName", member.getName()); //email로 조회한 회원 정보 객체를 member라는 키로 request에 attribute로 저장

            request.getRequestDispatcher("../blogs/blog-post-form.jsp").forward(request, response);

        } else if(action.equals("post.do")) {
            System.out.println("post.do");

            Blog blog = new Blog();
            blog.setName(request.getParameter("name"));
            blog.setEmail(request.getParameter("email"));
            blog.setTitle(request.getParameter("title"));
            blog.setContent(request.getParameter("content"));
            //데이터베이스 처리 요청 또는 서비스 요청 코드 추가

            if (dao.create(blog) > 0) {
                //처리된 결과를 애트리뷰트로 설정
                /*
                request.setAttribute("name", blog.getName());
                request.setAttribute("email", blog.getEmail());
                */
                request.setAttribute("blog", blog);
                //처리 결과를 view에 전달
                request.setAttribute("work", "블로그 작성을");
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        } else if(action.equals("list.do")){
            ArrayList<Blog> blogList = new ArrayList<Blog>(); //처리결과 한개 이상의 블로그를 저장하는 객체

            String pageNo = request.getParameter("pn");

            int curPageNo = (pageNo != null)? Integer.parseInt(pageNo) : 1; // 매개변수로 전달된 현재 페이지 번호가 정수형으로 저장
            int perPage = 3; // 한 페이지에 나타나는 행의 수
            int perPagination = 3; // 한 화면에 나타나는 페이지 번호 수

            int totalRows = dao.readTotalRows(); // dao에서 총 행의 수를 질의함

            Pagination pagination = new Pagination(curPageNo, perPage, perPagination, totalRows);

            if((blogList = (ArrayList<Blog>) dao.readListPagination(pagination)) != null){ //한 개 이상의 블로그가 반환, JCF(Java Collection Framework)에 대한 이해
                request.setAttribute("blogList", blogList);
                request.setAttribute("pagination", pagination);
                request.getRequestDispatcher("blog-list-view.jsp").forward(request, response); // blogs/list.jsp로 포워딩
            }else{
                request.setAttribute("errMsg", "블로그 목록 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
            }
        } else if (action.equals("detail.do")) {
            // ?email=이메일 : queryString으로 요청한 경우, email 파라미터에 이메일이라는 문자열 값을 전달
            // System.out.println(request.getParameter("email")); // 요청에 포함된 파라미터 중 email 파라미터 값을 접근
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));
            Blog retBlog = null;


            //HttpSession session = request.getSession();

            if((retBlog = dao.read(blog)) != null) {
                request.setAttribute("blog", retBlog); // key -> blog
                //session.setAttribute("blog", "로그인정보");
                request.getRequestDispatcher("blog-read-view.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "블로그 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); // 오류
            }
        } else if(action.equals("updateForm.do")){ // update 를 위한 정보 조회 후 view 에게 전달송
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));
            Blog retBlog = null;
            if((retBlog = dao.read(blog)) != null) {
                request.setAttribute("blog", retBlog); // key -> blog
                request.getRequestDispatcher("blog-update-form.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "블로그 업데이트를 위한 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); // 오류
            }
        } else if(action.equals("update.do")){ // dao 에게 업데이트를 요청
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));
            blog.setName(request.getParameter("name"));
            blog.setEmail(request.getParameter("email"));
            blog.setTitle(request.getParameter("title"));
            blog.setContent(request.getParameter("content"));

            if(dao.update(blog) > 0){
                request.setAttribute("blog", blog);
                request.setAttribute("work", "블로그 수정을");
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        } else if(action.equals("delete.do")) {
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));

            if (dao.delete(blog) > 0) {
                request.setAttribute("blog", blog);
                request.setAttribute("work", "블로그 삭제를");
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        } /*else if(action.equals("test.do")){ // test scripting elements
            ArrayList<Blog> blogList = new ArrayList<Blog>(); //처리결과 한개 이상의 블로그를 저장하는 객체

            if((blogList = (ArrayList<Blog>) dao.readList()) != null){ //한 개 이상의 블로그가 반환, JCF(Java Collection Framework)에 대한 이해
                request.setAttribute("blogList", blogList);
                request.getRequestDispatcher("test.jsp").forward(request, response); // blogs/list.jsp로 포워딩
            }else{
                request.setAttribute("errMsg", "블로그 목록 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
            }
        }*/ else if(action.equals("sort.do")){
            String properties = request.getParameter("by");
            ArrayList<Blog> blogList = new ArrayList<Blog>();

            String pageNo = request.getParameter("pn");

            int curPageNo = (pageNo != null)? Integer.parseInt(pageNo) : 1; // 매개변수로 전달된 현재 페이지 번호가 정수형으로 저장
            int perPage = 3; // 한 페이지에 나타나는 행의 수
            int perPagination = 3; // 한 화면에 나타나는 페이지 번호 수

            int totalRows = dao.readTotalRows(); // dao에서 총 행의 수를 질의함

            Pagination pagination = new Pagination(curPageNo, perPage, perPagination, totalRows);

            if((blogList = (ArrayList<Blog>) dao.readList()) != null) {
                if(properties.equals("desc,title")) {
                    Collections.sort(blogList, new DescByBlogTitle()); //블로그 제목 기준 내림차순
                    if((blogList = (ArrayList<Blog>) dao.sortListPaginationDT(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("blogList", blogList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "블로그 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("asc,title")){
                    Collections.sort(blogList, new AscByBlogTitle());
                    if((blogList = (ArrayList<Blog>) dao.sortListPaginationAT(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("blogList", blogList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "블로그 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("desc,email")){
                    Collections.sort(blogList, new DescByBlogEmail());
                    if((blogList = (ArrayList<Blog>) dao.sortListPaginationDE(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("blogList", blogList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "블로그 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("asc,email")){
                    Collections.sort(blogList, new AscByBlogEmail());
                    if((blogList = (ArrayList<Blog>) dao.sortListPaginationAE(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("blogList", blogList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "블로그 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }
                request.getRequestDispatcher("blog-list-view.jsp").forward(request,response);
            } else {
                request.setAttribute("errMsg", "블로그 목록 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doService(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doService(request, response);
    }
}
