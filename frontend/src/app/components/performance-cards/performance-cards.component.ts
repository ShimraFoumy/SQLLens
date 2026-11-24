import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PerformanceSummary } from '../../models/sqllens.models';

@Component({
  selector: 'app-performance-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './performance-cards.component.html',
  styleUrls: ['./performance-cards.component.css']
})
export class PerformanceCardsComponent {
  @Input() summary: PerformanceSummary | null = null;
}
