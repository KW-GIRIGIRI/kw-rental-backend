package com.girigiri.kwrental.rental.domain;

import java.time.LocalDateTime;

import com.girigiri.kwrental.common.AbstractSuperEntity;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Getter
@Table(name = "rental_spec")
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
        if (status != null)
            this.status = status;
        if (acceptDateTime == null)
            this.acceptDateTime = RentalDateTime.now();
        if (acceptDateTime != null)
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
