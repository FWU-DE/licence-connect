import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { HttpService } from '@nestjs/axios';
import { ucsResponseWithLicences, incomingVidisCoreRequest } from './test_data';
import { AxiosResponse } from 'axios';
import { MVLicenceService } from './mv/mv-licence.service';
import { MVLicenceFetcherService } from './mv/mv-licence-fetcher-service';
import { MVStudent } from './ucs-types';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  const buildSuccessfullResponse: (MVStudent) => AxiosResponse = (
    student: MVStudent,
  ) => {
    return {
      data: student,
      status: 200,
      statusText: 'OK',
      headers: undefined,
      config: undefined,
    };
  };

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [LicencesController],
      providers: [
        MVLicenceService,
        MVLicenceFetcherService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((url: string) => {
              if (
                url ===
                `/ucsschool/apis/bildungslogin/v1/user/${incomingVidisCoreRequest.sub}`
              ) {
                return buildSuccessfullResponse(ucsResponseWithLicences);
              }
            }),
          },
        },
      ],
    }).compile();

    licencesController = app.get<LicencesController>(LicencesController);
  });

  it('should be created"', () => {
    expect(licencesController).toBeTruthy;
  });
});
