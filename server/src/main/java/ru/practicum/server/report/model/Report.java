package ru.practicum.server.report.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.practicum.server.user.model.User;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
    @ManyToOne
    @JoinColumn(name = "reported_user")
    private User reportedUser;
    @Column(name = "reported_message", nullable = false)
    private String reportedMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Report report = (Report) o;
        return reportId != null && Objects.equals(reportId, report.reportId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
