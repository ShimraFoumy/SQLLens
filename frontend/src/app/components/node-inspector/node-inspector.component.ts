import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlanNode } from '../../models/sqllens.models';

@Component({
  selector: 'app-node-inspector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './node-inspector.component.html',
  styleUrls: ['./node-inspector.component.css']
})
export class NodeInspectorComponent {
  @Input() node: PlanNode | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() selectChild = new EventEmitter<PlanNode>();

  closeInspector(): void {
    this.close.emit();
  }

  onSelectChild(child: PlanNode): void {
    this.selectChild.emit(child);
  }

  getBufferRatio(): number {
    if (!this.node) return 100;
    const hits = this.node.sharedHitBlocks || 0;
    const reads = this.node.sharedReadBlocks || 0;
    const total = hits + reads;
    if (total === 0) return 100;
    return Math.round((hits / total) * 1000) / 10;
  }

  getRowsDeviationRatio(): number {
    if (!this.node || !this.node.planRows || !this.node.actualRows) return 1;
    return Math.round((this.node.actualRows / Math.max(1, this.node.planRows)) * 10) / 10;
  }
}
