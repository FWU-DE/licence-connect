import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { Observable } from 'rxjs';
import { ApiKeyService } from './api-key.service';

@Injectable()
export class ApiKeyGuard implements CanActivate {
  // apiKeyService: ApiKeyService;
  constructor(private readonly apiKeyService: ApiKeyService) {}
  /* constructor() {
    this.apiKeyService = new ApiKeyService();
  } */

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const req = context.switchToHttp().getRequest();
    const key =
      req.headers['X-API-KEY'] ??
      req.headers['x-api-key'] ??
      req.query['X-API-KEY'] ??
      req.query['x-api-key'];
    return this.apiKeyService.isApiKeyValid(key);
  }
}
