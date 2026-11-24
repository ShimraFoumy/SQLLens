import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OptimizationTip } from '../../models/sqllens.models';

@Component({
  selector: 'app-optimization-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './optimization-panel.component.html',
  styleUrls: ['./optimization-panel.component.css']
})
export class OptimizationPanelComponent {
  @Input() tips: OptimizationTip[] = [];
}
