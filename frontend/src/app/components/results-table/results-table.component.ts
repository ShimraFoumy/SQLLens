import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QueryResultResponse } from '../../models/sqllens.models';

@Component({
  selector: 'app-results-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './results-table.component.html',
  styleUrls: ['./results-table.component.css']
})
export class ResultsTableComponent implements OnChanges {
  @Input() result: QueryResultResponse | null = null;

  currentPage = 1;
  pageSize = 25;
  totalPages = 1;
  pagedRows: Record<string, any>[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['result'] && this.result) {
      this.currentPage = 1;
      this.updatePagination();
    }
  }

  updatePagination(): void {
    if (!this.result || !this.result.rows) {
      this.pagedRows = [];
      this.totalPages = 1;
      return;
    }

    const total = this.result.rows.length;
    this.totalPages = Math.max(1, Math.ceil(total / this.pageSize));
    const start = (this.currentPage - 1) * this.pageSize;
    this.pagedRows = this.result.rows.slice(start, start + this.pageSize);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  exportCsv(): void {
    if (!this.result || !this.result.rows.length) return;

    const headers = this.result.columns.map(c => `"${c.name}"`).join(',');
    const rows = this.result.rows.map(row => {
      return this.result!.columns.map(col => {
        const val = row[col.name];
        if (val === null || val === undefined) return '""';
        return `"${String(val).replace(/"/g, '""')}"`;
      }).join(',');
    });

    const csvContent = [headers, ...rows].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `sqllens_query_result_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
