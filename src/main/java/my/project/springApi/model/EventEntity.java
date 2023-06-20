package my.project.springApi.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class EventEntity extends BaseEntity {

    @EqualsAndHashCode.Exclude
    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "description")
    private Description description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileEntity fileEntity;

}
