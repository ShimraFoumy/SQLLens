
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  QueryResultResponse,
  ExplainResponse,
  TableMetadata,
  SampleQuery,
  QueryHistoryItem,
  CompareQueriesResponse
} from '../models/sqllens.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api';

  executeQuery(query: string): Observable<QueryResultResponse> {
    return this.http.post<QueryResultResponse>(
      `${this.baseUrl}/query/execute`,
      { query }
    );
  }

  explainQuery(
    query: string,
    analyze = true
  ): Observable<ExplainResponse> {
    return this.http.post<ExplainResponse>(
      `${this.baseUrl}/query/explain`,
      { query, analyze }
    );
  }

  compareQueries(
    queryA: string,
    queryB: string
  ): Observable<CompareQueriesResponse> {
    return this.http.post<CompareQueriesResponse>(
      `${this.baseUrl}/query/compare`,
      { queryA, queryB }
    );
  }

  getTables(): Observable<TableMetadata[]> {
    return this.http.get<TableMetadata[]>(
      `${this.baseUrl}/schema/tables`
    );
  }

  getSampleQueries(): Observable<SampleQuery[]> {
    return this.http.get<SampleQuery[]>(
      `${this.baseUrl}/schema/sample-queries`
    );
  }

  getHistory(): Observable<QueryHistoryItem[]> {
    return this.http.get<QueryHistoryItem[]>(
      `${this.baseUrl}/history`
    );
  }

  clearHistory(): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/history`
    );
  }
}
