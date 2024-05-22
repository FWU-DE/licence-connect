import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { LicenceService } from './licences.service';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [LicencesController],
      providers: [LicenceService],
    }).compile();

    licencesController = app.get<LicencesController>(LicencesController);
  });

  it('should be created"', () => {
    expect(licencesController).toBeTruthy;
  });

  it('should answer with with no licences"', () => {
    expect(licencesController.getLicences()).toStrictEqual({
      hasLicence: false,
      licences: [],
    });
  });
});
