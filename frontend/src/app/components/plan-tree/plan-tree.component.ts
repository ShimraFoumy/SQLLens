import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ElementRef,
  ViewChild,
  HostListener
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlanNode } from '../../models/sqllens.models';

interface LayoutNode {
  data: PlanNode;
  x: number;
  y: number;
  width: number;
  height: number;
  children: LayoutNode[];
}

interface TreeLink {
  source: LayoutNode;
  target: LayoutNode;
  path: string;
}

@Component({
  selector: 'app-plan-tree',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plan-tree.component.html',
  styleUrls: ['./plan-tree.component.css']
})
export class PlanTreeComponent implements OnChanges {
  @Input() rootNode: PlanNode | null = null;
  @Input() selectedNodeId: string | null = null;
  @Output() nodeSelected = new EventEmitter<PlanNode>();

  @ViewChild('svgContainer', { static: true }) svgContainer!: ElementRef<HTMLDivElement>;

  layoutNodes: LayoutNode[] = [];
  links: TreeLink[] = [];
  selectedNode: PlanNode | null = null;

  // SVG Canvas & Zoom/Pan state
  svgWidth = 1200;
  svgHeight = 800;
  zoom = 1;
  panX = 100;
  panY = 60;
  isDragging = false;
  dragStartX = 0;
  dragStartY = 0;

  readonly nodeWidth = 240;
  readonly nodeHeight = 115;
  readonly levelGap = 160;
  readonly siblingGap = 40;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['rootNode'] && this.rootNode) {
      this.computeLayout();
      this.resetView();
    }
    if (changes['selectedNodeId']) {
      this.syncSelectedNode();
    }
  }

  computeLayout(): void {
    if (!this.rootNode) {
      this.layoutNodes = [];
      this.links = [];
      return;
    }

    let leafXCursor = 40;

    // Helper to recursively assign coordinates
    const buildLayoutTree = (node: PlanNode, depth: number): LayoutNode => {
      const children: LayoutNode[] = [];
      if (node.plans && node.plans.length > 0) {
        for (const child of node.plans) {
          children.push(buildLayoutTree(child, depth + 1));
        }
      }

      let x: number;
      if (children.length === 0) {
        // Leaf node gets next sequential slot
        x = leafXCursor;
        leafXCursor += this.nodeWidth + this.siblingGap;
      } else if (children.length === 1) {
        x = children[0].x;
      } else {
        // Parent centered above children
        const minX = children[0].x;
        const maxX = children[children.length - 1].x;
        x = (minX + maxX) / 2;
      }

      const y = 40 + depth * this.levelGap;

      return {
        data: node,
        x,
        y,
        width: this.nodeWidth,
        height: this.nodeHeight,
        children
      };
    };

    const layoutRoot = buildLayoutTree(this.rootNode, 0);

    // Flatten nodes & generate links
    const flatNodes: LayoutNode[] = [];
    const flatLinks: TreeLink[] = [];

    const traverse = (node: LayoutNode) => {
      flatNodes.push(node);
      for (const child of node.children) {
        // Curve from bottom center of parent to top center of child
        const sourceX = node.x + node.width / 2;
        const sourceY = node.y + node.height;
        const targetX = child.x + child.width / 2;
        const targetY = child.y;

        const controlY1 = sourceY + (targetY - sourceY) * 0.5;
        const controlY2 = sourceY + (targetY - sourceY) * 0.5;

        const path = `M ${sourceX} ${sourceY} C ${sourceX} ${controlY1}, ${targetX} ${controlY2}, ${targetX} ${targetY}`;
        flatLinks.push({ source: node, target: child, path });
        traverse(child);
      }
    };

    traverse(layoutRoot);

    this.layoutNodes = flatNodes;
    this.links = flatLinks;

    // Adjust canvas boundaries
    const maxX = Math.max(...flatNodes.map(n => n.x + n.width), 1000);
    const maxY = Math.max(...flatNodes.map(n => n.y + n.height), 700);
    this.svgWidth = maxX + 100;
    this.svgHeight = maxY + 100;

    // Auto-select root by default if none selected
    if (!this.selectedNode && this.layoutNodes.length > 0) {
      this.selectNode(this.layoutNodes[0].data);
    }
  }

  selectNode(node: PlanNode): void {
    this.selectedNode = node;
    this.nodeSelected.emit(node);
  }

  syncSelectedNode(): void {
    if (!this.selectedNodeId) return;
    const match = this.layoutNodes.find(n => n.data.id === this.selectedNodeId);
    if (match) {
      this.selectedNode = match.data;
    }
  }

  // Color-coded classification
  getNodeColorClass(node: PlanNode): string {
    const type = (node.nodeType || '').toLowerCase();
    if (type.includes('index scan') || type.includes('index only')) return 'node-index';
    if (type.includes('seq scan')) {
      const costPct = node.costPercentage || 0;
      return costPct > 40 ? 'node-seq-critical' : 'node-seq-normal';
    }
    if (type.includes('join')) return 'node-join';
    if (type.includes('aggregate') || type.includes('group')) return 'node-aggregate';
    if (type.includes('sort')) return 'node-sort';
    return 'node-default';
  }

  getNodeBadgeLabel(node: PlanNode): string {
    const type = node.nodeType || 'Operation';
    if (type.length > 22) return type.substring(0, 20) + '...';
    return type;
  }

  // Pan and Zoom Controls
  startDrag(event: MouseEvent): void {
    if (event.button !== 0) return; // Left click only
    this.isDragging = true;
    this.dragStartX = event.clientX - this.panX;
    this.dragStartY = event.clientY - this.panY;
  }

  @HostListener('window:mousemove', ['$event'])
  onDrag(event: MouseEvent): void {
    if (!this.isDragging) return;
    this.panX = event.clientX - this.dragStartX;
    this.panY = event.clientY - this.dragStartY;
  }

  @HostListener('window:mouseup')
  endDrag(): void {
    this.isDragging = false;
  }

  onWheel(event: WheelEvent): void {
    event.preventDefault();
    const zoomFactor = event.deltaY < 0 ? 1.1 : 0.9;
    const newZoom = Math.min(Math.max(0.3, this.zoom * zoomFactor), 2.5);

    // Zoom towards center of container
    const rect = this.svgContainer.nativeElement.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;

    this.panX = mouseX - (mouseX - this.panX) * (newZoom / this.zoom);
    this.panY = mouseY - (mouseY - this.panY) * (newZoom / this.zoom);
    this.zoom = newZoom;
  }

  zoomIn(): void {
    this.zoom = Math.min(2.5, this.zoom * 1.2);
  }

  zoomOut(): void {
    this.zoom = Math.max(0.3, this.zoom / 1.2);
  }

  resetView(): void {
    this.zoom = 0.95;
    this.panX = 60;
    this.panY = 40;
  }
}
