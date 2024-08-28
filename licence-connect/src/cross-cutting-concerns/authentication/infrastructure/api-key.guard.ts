import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { Observable } from 'rxjs';

@Injectable()
export abstract class ApiKeyGuard implements CanActivate {
  constructor() {}

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const req = context.switchToHttp().getRequest();
    const key = this.extractApiKey(req);
    return this.isValidKey(key);
  }

  private extractApiKey(req: any): string | undefined {
    const key = req.headers['X-API-KEY'] ?? req.headers['x-api-key'];
    return key;
  }

  private isValidKey(providedApiKey?: string): boolean {
    const expectedApiKey = this.getApiKey();
    const isApiKeyDefined = providedApiKey !== undefined;
    const isApiKeyValid = expectedApiKey === providedApiKey;
    return isApiKeyDefined && isApiKeyValid;
  }

  protected abstract getApiKey(): string | undefined;
}
