
package com.example.productservice.filter;

import com.example.productservice.domain.QProduct;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.*;

import java.math.BigDecimal;

public class ProductPredicates {
    public static BooleanExpression byFilter(String q, String category, BigDecimal min, BigDecimal max){
        QProduct p = QProduct.product;
        BooleanExpression exp = Expressions.asBoolean(true).isTrue();
        if(q != null) exp = exp.and(p.name.containsIgnoreCase(q));
        if(category != null) exp = exp.and(p.category.equalsIgnoreCase(category));
        if(min != null) exp = exp.and(p.price.goe(min));
        if(max != null) exp = exp.and(p.price.loe(max));
        return exp;
    }
}
