export interface ColumnMetadata {
  name: string;
  typeName: string;
  typeCode: number;
}

export interface QueryResultResponse {
  executionTimeMs: number;
  rowsReturned: number;
  columns: ColumnMetadata[];
  rows: Record<string, any>[];
  truncated: boolean;
  message: string;
  status: string;
}

export interface PlanNode {
  id: string;
  nodeType: string;
  relationName?: string;
  schema?: string;
  alias?: string;

  startupCost?: number;
  totalCost?: number;
  planRows?: number;
  planWidth?: number;

  actualStartupTime?: number;
  actualTotalTime?: number;
  actualRows?: number;
  actualLoops?: number;

  exclusiveTimeMs?: number;
  costPercentage?: number;
  timePercentage?: number;

  filter?: string;
  rowsRemovedByFilter?: number;
  indexName?: string;
  indexCond?: string;
  scanDirection?: string;

  joinType?: string;
  hashCond?: string;
  sortKey?: string;
  sortMethod?: string;
  sortSpaceUsed?: string;
  sortSpaceType?: string;

  sharedHitBlocks?: number;
  sharedReadBlocks?: number;
  sharedDirtiedBlocks?: number;
  sharedWrittenBlocks?: number;

  plans?: PlanNode[];

  // Graph Layout Coordinates computed by Tree layout engine
  x?: number;
  y?: number;
  width?: number;
  height?: number;
}

export interface PerformanceSummary {
  planningTimeMs: number;
  executionTimeMs: number;
  totalCost: number;
  rowsReturned: number;
  totalOperations: number;
  primaryOperation: string;
  primaryScanType: string;
  bottleneckNode: string;
  bottleneckTimeMs: number;
  totalRowsScanned: number;
  totalRowsRemovedByFilter: number;
  bufferHitRatio: number;
  usesIndex: boolean;
}

export interface OptimizationTip {
  level: 'INFO' | 'WARNING' | 'CRITICAL' | 'SUCCESS';
  title: string;
  message: string;
  recommendation: string;
  affectedNode: string;
}

export interface ExplainResponse {
  summary: PerformanceSummary;
  plan: PlanNode;
  optimizationTips: OptimizationTip[];
  rawJson: string;
  rawText: string;
}

export interface ColumnInfo {
  name: string;
  dataType: string;
  primaryKey: boolean;
  foreignKey: boolean;
  nullable: boolean;
}

export interface TableMetadata {
  tableName: string;
  estimatedRowCount: number;
  columns: ColumnInfo[];
  indexes: string[];
}

export interface SampleQuery {
  id: string;
  title: string;
  category: string;
  description: string;
  expectedPlanType: string;
  query: string;
}

export interface QueryHistoryItem {
  id: string;
  query: string;
  executionTimeMs: number;
  planningTimeMs: number;
  rowsReturned: number;
  status: string;
  errorMessage?: string;
  primaryOperation?: string;
  timestamp: string;
}

export interface CompareQueriesResponse {
  planA: ExplainResponse;
  planB: ExplainResponse;
  executionTimeDiffMs: number;
  costDiff: number;
  fasterQuery: 'QUERY_A' | 'QUERY_B' | 'TIE';
  speedupFactor: number;
  comparisonSummary: string;
}
