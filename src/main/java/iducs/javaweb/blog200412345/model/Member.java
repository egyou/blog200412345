package iducs.javaweb.blog200412345.model;


import java.util.Objects;

public class Member { //model 객체 : dto, vo 객체
    //객체 정의 방식: Beans, POJO(Plain Old Java Object)
    private long id;
    private String email;
    private String pw;
    private String name;
    private String phone;
    private String address;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPw() { return pw; }

    public void setPw(String pw) { this.pw = pw; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 문자들 시퀀스들의  통해 객체의 동일성을 반환하는 메소드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id && email.equals(member.email);
    }

    /**
     * 객체를 식별하가 위한 해시값 생성 반환하는 메소드
     * 객체 비교 연산에 활용하면 equals()보다 성능이 우수함
     */
    @Override
    public int hashCode() { return Objects.hash(id, email); }

    /**
     * 객체를 문자열화 값을 반환하는 메소드
     * @return
     */
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", pw='" + pw + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
