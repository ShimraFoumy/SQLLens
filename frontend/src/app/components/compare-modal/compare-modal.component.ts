import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CompareQueriesResponse } from '../../models/sqllens.models';
import { PlanTreeComponent } from '../plan-tree/plan-tree.component';

@Component({
  selector: 'app-compare-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, PlanTreeComponent],
  templateUrl: './compare-modal.component.html',
  styleUrls: ['./compare-modal.component.css']
})
export class CompareModalComponent {
  private readonly apiService = inject(ApiService);

  @Output() close = new EventEmitter<void>();

  queryA = `SELECT * FROM orders WHERE customer_id = 100;`;
  queryB = `SELECT * FROM orders WHERE customer_id = 100 ORDER BY order_date;`;

  isLoading = false;
  errorMessage: string | null = null;
  comparisonResult: CompareQueriesResponse | null = null;

  runComparison(): void {
    if (!this.queryA.trim() || !this.queryB.trim()) {
      this.errorMessage = 'Both queries are required for comparison.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.apiService.compareQueries(this.queryA, this.queryB).subscribe({
      next: (res) => {
        this.comparisonResult = res;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.message || 'Failed to compare queries.';
        this.isLoading = false;
      }
    });
  }

  closeModal(): void {
    this.close.emit();
  }
}
