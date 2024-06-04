import { Test, TestingModule } from '@nestjs/testing';
import { MVLicenceService } from './mv-licence.service';
import {
  lcResponseFromUCSResponse,
  ucsResponseWithLicences,
  ucsResponseWithoutLicences,
} from '../test_data';

describe('LicenseService', () => {
  let licensesService: MVLicenceService;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      providers: [MVLicenceService],
    }).compile();

    licensesService = app.get<MVLicenceService>(MVLicenceService);
  });

  it('should be created"', () => {
    expect(licensesService).toBeTruthy;
  });

  it('should extract all licenses"', () => {
    // act
    const extractedLicenses = licensesService.extractLicenceData(
      ucsResponseWithLicences,
    );

    // assert
    expect(extractedLicenses).toMatchObject({
      hasLicense: true,
      licenses: lcResponseFromUCSResponse,
    });
  });

  it('should extract no licenses"', () => {
    expect(
      licensesService.extractLicenceData(ucsResponseWithoutLicences),
    ).toStrictEqual({
      hasLicense: false,
      licenses: [],
    });
  });
});
