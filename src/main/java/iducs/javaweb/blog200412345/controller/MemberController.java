package iducs.javaweb.blog200412345.controller;

import iducs.javaweb.blog200412345.model.Member;
import iducs.javaweb.blog200412345.repository.DAOImplMysql;
import iducs.javaweb.blog200412345.repository.MemberDAOImpl;
import iducs.javaweb.blog200412345.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "members", urlPatterns = { "/members/detail.do", "/members/login-form.do", "/members/login.do", "/members/post-form.do",
        "/members/post.do", "/members/logout.do", "/members/list.do", "/members/update-form.do", "/members/update.do", "/members/delete.do" }) //urlPatterns : 다수의 url 을 기술할 수 있다.
public class MemberController extends HttpServlet {

    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String command = uri.substring(contextPath.length() + 1); // blogs/post.do, blog/list.do가 반환됨
        String action = command.substring(command.lastIndexOf("/") + 1); // post.do, list.do 반환

        //데이터베이스 처리 요청 또는 서비스 요청 코드 추가
        MemberDAOImpl dao = new MemberDAOImpl();
        HttpSession session = request.getSession();

        if(action.equals("post-form.do")) {
            DAOImplMysql mysql = new DAOImplMysql();
            mysql.getConnection();
            request.getRequestDispatcher("../members/member-post-form.jsp").forward(request, response);

        } else if (action.equals("post.do")) {
            Member member = new Member();
            member.setEmail(request.getParameter("email"));
            member.setPw(request.getParameter("pw"));
            member.setName(request.getParameter("name"));
            member.setPhone(request.getParameter("phone"));
            member.setAddress(request.getParameter("address"));

            if (dao.create(member) > 0) {
                //처리된 결과를 애트리뷰트로 설정
                /*
                request.setAttribute("name", blog.getName());
                request.setAttribute("email", blog.getEmail());
                */
                request.setAttribute("member", member);
                //처리 결과를 view에 전달
                request.setAttribute("work", "회원 가입을");
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "회원 가입을");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        } else if (action.equals("login-form.do")) {

            request.getRequestDispatcher("../members/memebr-login-form.jsp").forward(request, response);

        } else if (action.equals("login.do")) {
            // ?email=이메일 : queryString으로 요청한 경우, email 파라미터에 이메일이라는 문자열 값을 전달
            // System.out.println(request.getParameter("email")); // 요청에 포함된 파라미터 중 email 파라미터 값을 접근
            Member member = new Member();
            member.setEmail(request.getParameter("email"));
            member.setPw(request.getParameter("pw"));

            Member retMember = null;

            if ((retMember = dao.read(member)) != null) {
                // request.setAttribute("blog", retMember); // key -> blog
                session.setAttribute("logined", retMember); // 키값을 logined로 바꿨음, ${sessionScope.member}
                //session.setAttribute("member", member); //${sessionScope.member.email}
                if(retMember.getEmail().equals("sw@induk.ac.kr")){
                    session.setAttribute("admin", retMember);
                }
                request.getRequestDispatcher("../main/index.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "로그인을");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); // 오류
            }
        } else if (action.equals("logout.do")) {
            session.invalidate(); // session 객체를 무효화 (메모리에 존재하지 않으므로 접근 안됨)
            request.getRequestDispatcher("../main/index.jsp").forward(request, response);
        } else if (action.equals("detail.do")) {
            Member member = new Member();
            member.setEmail(request.getParameter("email"));

            Member retMember = null;

            if ((retMember = dao.readByEmail(member)) != null) {
                request.setAttribute("member", retMember); //email로 조회한 회원 정보 객체를 member라는 키로 request에 attribute로 저장
                // ${requestScope.member}
                request.getRequestDispatcher("../members/member-read-view.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "정보 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); // 오류
            }
        } else if(action.equals("list.do")){
            String properties = request.getParameter("by");
            ArrayList<Member> memberList = new ArrayList<Member>(); //처리결과 한개 이상의 블로그를 저장하는 객체

            String pageNo = request.getParameter("pn");

            int curPageNo = (pageNo != null)? Integer.parseInt(pageNo) : 1; // 매개변수로 전달된 현재 페이지 번호가 정수형으로 저장
            int perPage = 3; // 한 페이지에 나타나는 행의 수
            int perPagination = 3; // 한 화면에 나타나는 페이지 번호 수

            int totalRows = dao.readTotalRows(); // dao에서 총 행의 수를 질의함

            Pagination pagination = new Pagination(curPageNo, perPage, perPagination, totalRows);

            if((memberList = (ArrayList<Member>) dao.readListPagination(pagination)) != null){ //한 개 이상의 블로그가 반환, JCF(Java Collection Framework)에 대한 이해
                if(properties.equals("desc,name")) {
                    if((memberList = (ArrayList<Member>) dao.sortListPaginationDN(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("memberList", memberList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "회원 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("asc,name")){
                    if((memberList = (ArrayList<Member>) dao.sortListPaginationAN(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("memberList", memberList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "회원 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("desc,email")){
                    if((memberList = (ArrayList<Member>) dao.sortListPaginationDE(pagination)) != null){
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("memberList", memberList);
                        request.setAttribute("pagination", pagination);
                    }else{
                        request.setAttribute("errMsg", "회원 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }else if(properties.equals("asc,email")) {
                    if ((memberList = (ArrayList<Member>) dao.sortListPaginationAE(pagination)) != null) {
                        // 블로그 정렬이랑 페이지 한번에 처리
                        request.setAttribute("by", properties);
                        request.setAttribute("memberList", memberList);
                        request.setAttribute("pagination", pagination);
                    } else {
                        request.setAttribute("errMsg", "회원 목록 조회를");
                        request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
                    }
                }
                request.getRequestDispatcher("member-list-view.jsp").forward(request, response); // blogs/list.jsp로 포워딩
            }else{
                request.setAttribute("errMsg", "멤버 목록 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); //오류
            }
        } else if(action.equals("update-form.do")){
            Member member = new Member();
            member.setEmail(request.getParameter("email"));

            Member retMember = null;

            if ((retMember = dao.readByEmail(member)) != null) {
                request.setAttribute("member", retMember); //email로 조회한 회원 정보 객체를 member라는 키로 request에 attribute로 저장
                // ${requestScope.member}
                request.getRequestDispatcher("../members/member-update-form.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "정보 조회를");
                request.getRequestDispatcher("../status/error.jsp").forward(request, response); // 오류
            }
        } else if(action.equals("update.do")){
            Member member = new Member();
            member.setId(Long.parseLong(request.getParameter("id")));
            member.setEmail(request.getParameter("email"));
            member.setPw(request.getParameter("pw"));
            member.setName(request.getParameter("name"));
            member.setPhone(request.getParameter("phone"));
            member.setAddress(request.getParameter("address"));

            if(dao.update(member) > 0){
                request.setAttribute("member", member);
                request.setAttribute("work", "회원정보 수정을");
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        } else if(action.equals("delete.do")){
            Member member = new Member();
            member.setEmail(request.getParameter("email"));

            if (dao.delete(member) > 0) {
                request.setAttribute("member", member);
                request.setAttribute("work", "회원 탈퇴를");
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                session.invalidate(); // session 객체를 무효화 (메모리에 존재하지 않으므로 접근 안됨)
                request.getRequestDispatcher("../status/message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("../status/error.jsp").forward(request, response);
            }
        }
        /*
        else if(action.equals("list.do")){
            ArrayList<Blog> blogList = new ArrayList<Blog>(); //처리결과 한개 이상의 블로그를 저장하는 객체

            if((blogList = (ArrayList<Blog>) dao.readList()) != null){ //한 개 이상의 블로그가 반환, JCF(Java Collection Framework)에 대한 이해
                request.setAttribute("blogList", blogList);
                request.getRequestDispatcher("blog-list-view.jsp").forward(request, response); // blogs/list.jsp로 포워딩
            }else{
                request.setAttribute("errMsg", "블로그 목록 조회 실패");
                request.getRequestDispatcher("error.jsp").forward(request, response); //오류
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
                request.getRequestDispatcher("member-read-view.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "블로그 조회 실패");
                request.getRequestDispatcher("error.jsp").forward(request, response); // 오류
            }
        } else if(action.equals("updateForm.do")){ // update 를 위한 정보 조회 후 view 에게 전달송
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));
            Blog retBlog = null;
            if((retBlog = dao.read(blog)) != null) {
                request.setAttribute("blog", retBlog); // key -> blog
                request.getRequestDispatcher("blog-update-form.jsp").forward(request, response);
            } else {
                request.setAttribute("errMsg", "블로그 업데이트를 위한 조회 실패");
                request.getRequestDispatcher("error.jsp").forward(request, response); // 오류
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
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                request.getRequestDispatcher("message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        } else if(action.equals("delete.do")) {
            Blog blog = new Blog();
            blog.setId(Long.parseLong(request.getParameter("id")));

            if (dao.delete(blog) > 0) {
                request.setAttribute("blog", blog);
                request.setAttribute("work", "블로그 삭제");
                // 처리 결과를 view에 전달한다. message.jsp -> processok.jsp
                request.getRequestDispatcher("message.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        } else if(action.equals("test.do")){ // test scripting elements
            ArrayList<Blog> blogList = new ArrayList<Blog>(); //처리결과 한개 이상의 블로그를 저장하는 객체

            if((blogList = (ArrayList<Blog>) dao.readList()) != null){ //한 개 이상의 블로그가 반환, JCF(Java Collection Framework)에 대한 이해
                request.setAttribute("blogList", blogList);
                request.getRequestDispatcher("test.jsp").forward(request, response); // blogs/list.jsp로 포워딩
            }else{
                request.setAttribute("errMsg", "블로그 목록 조회 실패");
                request.getRequestDispatcher("error.jsp").forward(request, response); //오류
            }
        }*/
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
