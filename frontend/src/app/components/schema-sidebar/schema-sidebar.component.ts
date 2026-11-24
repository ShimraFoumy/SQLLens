import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableMetadata } from '../../models/sqllens.models';

@Component({
  selector: 'app-schema-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './schema-sidebar.component.html',
  styleUrls: ['./schema-sidebar.component.css']
})
export class SchemaSidebarComponent {
  @Input() tables: TableMetadata[] = [];
  @Output() insertSql = new EventEmitter<string>();
  @Output() close = new EventEmitter<void>();

  searchQuery = '';
  expandedTables: Record<string, boolean> = {
    customers: true,
    orders: true
  };

  toggleTable(tableName: string): void {
    this.expandedTables[tableName] = !this.expandedTables[tableName];
  }

  get filteredTables(): TableMetadata[] {
    if (!this.searchQuery.trim()) return this.tables;
    const q = this.searchQuery.toLowerCase();
    return this.tables.filter(t =>
      t.tableName.toLowerCase().includes(q) ||
      t.columns.some(c => c.name.toLowerCase().includes(q))
    );
  }

  onInsert(text: string): void {
    this.insertSql.emit(text);
  }
}
