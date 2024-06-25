import { LicenceManagementApiKeyGuard } from './licence-management-api-key.guard';

describe('LicenceManagementApiKeyGuard', () => {
  it('should be defined', () => {
    expect(new LicenceManagementApiKeyGuard()).toBeDefined();
  });
});
