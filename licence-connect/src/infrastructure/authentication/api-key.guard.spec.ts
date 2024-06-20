import { ApiKeyGuard } from './api-key.guard';
import { ApiKeyService } from './api-key.service';

describe('ApiKeyGuard', () => {
  it('should be defined', () => {
    expect(new ApiKeyGuard(new ApiKeyService())).toBeDefined();
  });
});
