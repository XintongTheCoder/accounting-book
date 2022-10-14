import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Category } from 'src/app/data-types';

export interface FilterChange {
  text: string;
  category: Category;
}

@Component({
  selector: 'app-item-filter-bar',
  templateUrl: './item-filter-bar.component.html',
  styleUrls: ['./item-filter-bar.component.scss'],
})
export class FilterBarComponent implements OnDestroy {
  filterForm: FormGroup;
  categories = Object.values(Category);
  private readonly destroy = new Subject<void>();

  @Output()
  readonly filterChange = new EventEmitter<FilterChange>();

  constructor(private formBuilder: FormBuilder) {
    this.filterForm = formBuilder.group({
      text: [],
      category: [Category.ALL],
    });
    this.filterForm.valueChanges
      .pipe(takeUntil(this.destroy))
      .subscribe((v) => {
        this.filterChange.next(v);
      });
  }

  ngOnDestroy(): void {
    this.destroy.next();
  }
}
