package site.metacoding.bank.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass // 엔티티 공통 필드가 존재할 때 중복 코드를 제거하기 위해 사용
@EntityListeners(AuditingEntityListener.class)
public abstract class AudingTime {
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public LocalDateTime getUpdatedAt() {
        updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return createdAt;
    }

}
