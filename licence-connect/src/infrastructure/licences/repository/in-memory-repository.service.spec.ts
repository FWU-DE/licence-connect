import { Test, TestingModule } from '@nestjs/testing';
import { InMemoryRepositoryService } from './in-memory-repository.service';

describe('InMemoryRepositoryService', () => {
  let service: InMemoryRepositoryService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [InMemoryRepositoryService],
    }).compile();

    service = module.get<InMemoryRepositoryService>(InMemoryRepositoryService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('get licences', () => {
    it('get empty list for non-existing user', () => {
      expect(service.getLicencesForStudentId('test')).toMatchObject([]);
    });
  });

  describe('add licences', () => {
    it('add empty list', () => {
      service.addLicencesForStudentId('test', []);

      expect(service.getLicencesForStudentId('test')).toMatchObject([]);
    });

    it('add multiple licences', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111' },
        { license_code: '112' },
        { license_code: '113' },
      ]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '111' },
        { license_code: '112' },
        { license_code: '113' },
      ]);
    });

    it('add multiple licences sequentially', () => {
      service.addLicencesForStudentId('test', [{ license_code: '111' }]);

      service.addLicencesForStudentId('test', [{ license_code: '112' }]);

      service.addLicencesForStudentId('test', [{ license_code: '113' }]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '111' },
        { license_code: '112' },
        { license_code: '113' },
      ]);
    });
  });

  fdescribe('remove licences', () => {
    it('remove multiple licences', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111' },
        { license_code: '112' },
        { license_code: '113' },
      ]);

      service.removeLicencesForStudentId('test', [
        { license_code: '111' },
        { license_code: '112' },
      ]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '113' },
      ]);
    });

    it('remove all licences', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111' },
        { license_code: '112' },
        { license_code: '113' },
      ]);

      service.removeAllLicencesForStudentId('test');

      expect(service.getLicencesForStudentId('test')).toMatchObject([]);
    });
  });
});
