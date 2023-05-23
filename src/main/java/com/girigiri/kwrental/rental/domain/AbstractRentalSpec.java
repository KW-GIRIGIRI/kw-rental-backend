package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.common.AbstractSuperEntity;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Getter
@Table(name = "rental_spec")
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractRentalSpec extends AbstractSuperEntity implements RentalSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationSpecId;

    @Column(nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalSpecStatus status = RentalSpecStatus.RENTED;

    @CreatedDate
    @Embedded
    @AttributeOverride(name = "instant", column = @Column(name = "accept_date_time"))
    private RentalDateTime acceptDateTime;

    @Embedded
    @AttributeOverride(name = "instant", column = @Column(name = "return_date_time"))
    private RentalDateTime returnDateTime;

    protected AbstractRentalSpec() {
    }

    protected AbstractRentalSpec(final Long id, final Long reservationSpecId, final Long reservationId,
                                 final RentalSpecStatus status, final RentalDateTime acceptDateTime, final RentalDateTime returnDateTime) {
        this.id = id;
        this.reservationSpecId = reservationSpecId;
        this.reservationId = reservationId;
        if (status != null) this.status = status;
        this.acceptDateTime = acceptDateTime;
        this.returnDateTime = returnDateTime;
    }

    @Override
    public boolean isNowRental() {
        return acceptDateTime != null && returnDateTime == null;
    }

    @Override
    public void setStatus(final RentalSpecStatus status) {
        this.status = status;
    }

    @Override
    public void setReturnDateTimeIfAnyReturned(final LocalDateTime returnDateTime) {
        if (this.status.isReturnedOrAbnormalReturned()) {
            this.returnDateTime = RentalDateTime.from(returnDateTime);
        }
    }

    @Override
    public boolean isUnavailableAfterReturn() {
        return this.status == RentalSpecStatus.LOST || this.status == RentalSpecStatus.BROKEN || this.status == RentalSpecStatus.OVERDUE_RENTED;
    }

    @Override
    public boolean isOverdueReturned() {
        return this.status == RentalSpecStatus.OVERDUE_RETURNED;
    }
}
