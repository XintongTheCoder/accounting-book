package com.xintongthecoder.accountingbook.modelAssembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.xintongthecoder.accountingbook.controller.SpendingItemController;
import com.xintongthecoder.accountingbook.entity.SpendingItem;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SpendingItemModelAssembler
                implements RepresentationModelAssembler<SpendingItem, EntityModel<SpendingItem>> {
        @Override
        public EntityModel<SpendingItem> toModel(SpendingItem item) {
                Long bookId = item.getBook().getId();
                String email = item.getBook().getAccount().getEmail();
                return EntityModel.of(item,
                                linkTo(methodOn(SpendingItemController.class).one(email, bookId,
                                                item.getId(), null, null, null)).withSelfRel(),
                                linkTo(methodOn(SpendingItemController.class).all(email, bookId,
                                                null, null, null, null, null)).withRel("items"));
        }
}
