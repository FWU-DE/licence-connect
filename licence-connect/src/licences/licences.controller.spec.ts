import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { LicenceService } from './licences.service';
import { HttpService } from '@nestjs/axios';
import { incomingVidisCoreRequest } from './test_data';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [LicencesController],
      providers: [
        LicenceService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn(),
          },
        },
      ],
    }).compile();

    licencesController = app.get<LicencesController>(LicencesController);
  });

  it('should be created"', () => {
    expect(licencesController).toBeTruthy;
  });

  it('should answer with with no licences"', () => {
    expect(
      licencesController.getLicences({ body: incomingVidisCoreRequest }),
    ).toStrictEqual({
      hasLicence: false,
      licences: [],
    });
  });
});
