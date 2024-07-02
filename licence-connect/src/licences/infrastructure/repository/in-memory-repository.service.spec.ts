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
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
        { license_code: '113', educationalOffer: 'BA1' },
      ]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
        { license_code: '113', educationalOffer: 'BA1' },
      ]);
    });

    it('add multiple licences sequentially', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111', educationalOffer: 'BA1' },
      ]);

      service.addLicencesForStudentId('test', [
        { license_code: '112', educationalOffer: 'BA1' },
      ]);

      service.addLicencesForStudentId('test', [
        { license_code: '113', educationalOffer: 'BA1' },
      ]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
        { license_code: '113', educationalOffer: 'BA1' },
      ]);
    });
  });

  describe('remove licences', () => {
    it('remove multiple licences', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
        { license_code: '113', educationalOffer: 'BA1' },
      ]);

      service.removeLicencesForStudentId('test', [
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
      ]);

      expect(service.getLicencesForStudentId('test')).toMatchObject([
        { license_code: '113', educationalOffer: 'BA1' },
      ]);
    });

    it('remove all licences', () => {
      service.addLicencesForStudentId('test', [
        { license_code: '111', educationalOffer: 'BA1' },
        { license_code: '112', educationalOffer: 'BA1' },
        { license_code: '113', educationalOffer: 'BA1' },
      ]);

      service.removeAllLicencesForStudentId('test');

      expect(service.getLicencesForStudentId('test')).toMatchObject([]);
    });
  });
});
