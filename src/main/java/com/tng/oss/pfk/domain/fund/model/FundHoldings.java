package com.tng.oss.pfk.domain.fund.model;

import com.tng.oss.pfk.infrastructure.core.persistence.BaseEntity;
import com.tng.oss.pfk.infrastructure.core.validation.GenericAssertions;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_fund_date", columnNames = {"fundId", "reportDate"}),
        },
        indexes = {
                @Index(name = "idx_report_date", columnList = "reportDate")
        }
)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@Setter(AccessLevel.NONE)
@Builder
public class FundHoldings extends BaseEntity {
    @NotNull
    @Positive
    @Column(nullable = false, updatable = false)
    private final Long fundId;

    @NotNull
    @Past
    private final LocalDate reportDate;

    @NotEmpty
    @ElementCollection
    @CollectionTable(
            name = "FUND_HOLDING_ITEMS",
            joinColumns = {@JoinColumn(name = "HOLDING_GROUP_ID")},
            uniqueConstraints = @UniqueConstraint(name = "uq_group_stock", columnNames = {"HOLDING_GROUP_ID", "stockId"})
    )
    private Set<FundHoldingItem> holdingItems;

    public void addHoldingItem(@NotNull FundHoldingItem holdingItem) {
        GenericAssertions.notNull(holdingItem, "Fund holding item cannot be null!");
        if (holdingItems == null) {
            this.holdingItems = new TreeSet<FundHoldingItem>(Comparator.comparing(FundHoldingItem::getNetAssetValueRatio).reversed())
                    ;
        }
        holdingItem.setReportDate(reportDate);
        holdingItems.add(holdingItem);
    }


    public Set<FundHoldingItem> getHoldingItems() {
        if (holdingItems == null || holdingItems.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(holdingItems);
    }
}
