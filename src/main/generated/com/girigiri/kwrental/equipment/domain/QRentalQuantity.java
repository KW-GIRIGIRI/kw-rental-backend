package com.girigiri.kwrental.equipment.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRentalQuantity is a Querydsl query type for RentalQuantity
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QRentalQuantity extends BeanPath<RentalQuantity> {

    private static final long serialVersionUID = 1075111466L;

    public static final QRentalQuantity rentalQuantity = new QRentalQuantity("rentalQuantity");

    public final NumberPath<Integer> remainQuantity = createNumber("remainQuantity", Integer.class);

    public final NumberPath<Integer> totalQuantity = createNumber("totalQuantity", Integer.class);

    public QRentalQuantity(String variable) {
        super(RentalQuantity.class, forVariable(variable));
    }

    public QRentalQuantity(Path<? extends RentalQuantity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRentalQuantity(PathMetadata metadata) {
        super(RentalQuantity.class, metadata);
    }

}

