package iducs.javaweb.blog200412345.repository;

import iducs.javaweb.blog200412345.model.Member;
import iducs.javaweb.blog200412345.util.Pagination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl extends DAOImplOracle implements MemberDAO{

    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    public MemberDAOImpl(){
        conn = getConnection();
    }

    @Override
    public int create(Member member) {
        String query = "insert into blogger200412345 values(seq_blogger200412345.nextval, ?, ?, ?, ?, ?)";
        int rows = 0; // 질의 처리 결과 영향받은 행의 수
        try{
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPw());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getAddress());
            rows = pstmt.executeUpdate();// 1 이상이면 정상, 0 이하면 오류
        } catch(SQLException e){
            e.printStackTrace();
        }
        return rows;
    }

    @Override
    public Member read(Member member) {
        Member retMember = null;
        // 지난주 email 조건 -> id 조건으로 조회
        String sql = "select * from blogger200412345 where email=? and pw=?"; // 유일키로(unique key)로 조회
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPw());
            rs = pstmt.executeQuery();
            if(rs.next()) { // rs.next()는 반환된 객체에 속한 요소가 있는지를 반환하고, 다름 요소로 접근
                retMember = new Member();
                retMember.setId(rs.getLong("id"));
                retMember.setEmail(rs.getString("email"));
                retMember.setPw(rs.getString("pw"));
                retMember.setName(rs.getString("name"));
                retMember.setPhone(rs.getString("phone"));
                retMember.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retMember;
    }

    @Override
    public List<Member> readList() {
        ArrayList<Member> memberList = null;
        String sql = "select * from blogger200412345";
        try{
            stmt = conn.createStatement();
            if((rs = stmt.executeQuery(sql)) != null){
                memberList = new ArrayList<Member>();
                while(rs.next()) {
                    Member member = new Member();
                    member = setMemberRs(rs);
                    memberList.add(member);
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return memberList;
    }

    private Member setMemberRs(ResultSet rs) throws SQLException{
        Member retMember = new Member();
        retMember.setId(rs.getLong(1));
        retMember.setEmail(rs.getString(2));
        retMember.setPw(rs.getString(3));
        retMember.setName(rs.getString(4));
        retMember.setPhone(rs.getString(5));
        retMember.setAddress(rs.getString(6));

        return retMember;
    }

    @Override
    public Member readByEmail(Member member) {
        Member retMember = null;
        // 지난주 email 조건 -> id 조건으로 조회
        String sql = "select * from blogger200412345 where email=?"; // 유일키로(unique key)로 조회
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getEmail());
            rs = pstmt.executeQuery();
            if(rs.next()) { // rs.next()는 반환된 객체에 속한 요소가 있는지를 반환하고, 다름 요소로 접근
                retMember = new Member();
                retMember.setId(rs.getLong("id"));
                retMember.setEmail(rs.getString("email"));
                retMember.setPw(rs.getString("pw"));
                retMember.setName(rs.getString("name"));
                retMember.setPhone(rs.getString("phone"));
                retMember.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retMember;
    }

    @Override
    public int update(Member member) {
        int retMember = 0;
        conn = this.getConnection();
        String sql = "update blogger200412345 set pw=?, name=?, phone=?, address=? where email=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getPw());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getAddress());
            pstmt.setString(5, member.getEmail());
            retMember = pstmt.executeUpdate();
        } catch (SQLException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            this.closeResources(conn, stmt, pstmt, rs);
        }
        return retMember;
    }

    @Override
    public int delete(Member member) {
        // TODO Auto-generated method stub
        int ret = 0;
        conn = this.getConnection();
        String sql = "delete from blogger200412345 where email=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getEmail());
            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            this.closeResources(conn, stmt, pstmt, rs);
        }
        return ret;
    }

    @Override
    public List<Member> readListPagination(Pagination pagination) {
        ArrayList<Member> memberList = null;
        String sql = "select * from (select A.*, rownum as rnum from (select * from blogger200412345 where email not in ('sw@induk.ac.kr') order by id desc) A) where rnum >= ? and rnum <= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pagination.getFirstRow());
            pstmt.setInt(2, pagination.getEndRow());
            rs = pstmt.executeQuery();
            memberList = new ArrayList<Member>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id")); // id 값도 dto에 저장
                member.setEmail(rs.getString("email"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public int readTotalRows() {
        int rows = 0;
        String sql = "select count(*) as totalRows from blogger200412345";

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rows = rs.getInt("totalRows");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    @Override
    public List<Member> sortListPaginationDN(Pagination pagination) {
        ArrayList<Member> memberList = null;
        String sql = "select * from (select A.*, rownum as rnum from (select * from blogger200412345 where email not in ('sw@induk.ac.kr') order by name desc) A) where rnum >= ? and rnum <= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pagination.getFirstRow());
            pstmt.setInt(2, pagination.getEndRow());
            rs = pstmt.executeQuery();
            memberList = new ArrayList<Member>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id")); // id 값도 dto에 저장
                member.setEmail(rs.getString("email"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public List<Member> sortListPaginationAN(Pagination pagination) {
        ArrayList<Member> memberList = null;
        String sql = "select * from (select A.*, rownum as rnum from (select * from blogger200412345 where email not in ('sw@induk.ac.kr') order by name) A) where rnum >= ? and rnum <= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pagination.getFirstRow());
            pstmt.setInt(2, pagination.getEndRow());
            rs = pstmt.executeQuery();
            memberList = new ArrayList<Member>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id")); // id 값도 dto에 저장
                member.setEmail(rs.getString("email"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public List<Member> sortListPaginationDE(Pagination pagination) {
        ArrayList<Member> memberList = null;
        String sql = "select * from (select A.*, rownum as rnum from (select * from blogger200412345 where email not in ('sw@induk.ac.kr') order by email desc) A) where rnum >= ? and rnum <= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pagination.getFirstRow());
            pstmt.setInt(2, pagination.getEndRow());
            rs = pstmt.executeQuery();
            memberList = new ArrayList<Member>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id")); // id 값도 dto에 저장
                member.setEmail(rs.getString("email"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public List<Member> sortListPaginationAE(Pagination pagination) {
        ArrayList<Member> memberList = null;
        String sql = "select * from (select A.*, rownum as rnum from (select * from blogger200412345 where email not in ('sw@induk.ac.kr') order by email) A) where rnum >= ? and rnum <= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pagination.getFirstRow());
            pstmt.setInt(2, pagination.getEndRow());
            rs = pstmt.executeQuery();
            memberList = new ArrayList<Member>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id")); // id 값도 dto에 저장
                member.setEmail(rs.getString("email"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }
}
