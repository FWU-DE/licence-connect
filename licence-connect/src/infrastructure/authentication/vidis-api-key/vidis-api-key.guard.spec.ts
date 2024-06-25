import { VidisApiKeyGuard } from './vidis-api-key.guard';

describe('VidisApiKeyGuard', () => {
  it('should be defined', () => {
    expect(new VidisApiKeyGuard()).toBeDefined();
  });
});
