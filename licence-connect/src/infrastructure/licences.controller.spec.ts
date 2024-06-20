import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { HttpService } from '@nestjs/axios';
import {
  ucsResponseWithLicences,
  incomingVidisCoreRequest,
} from '../domain/ucs/example-data';
import { AxiosResponse } from 'axios';
import { UCSStudent } from 'domain/ucs/ucs-types';
import { UCSLicenceFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  const buildSuccessfullResponse: (ucsStudent: UCSStudent) => AxiosResponse = (
    student: UCSStudent,
  ) => {
    return {
      data: { 'http://www.bildungslogin.de/licenses': student },
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
        UCSLicenceFetcherService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((url: string) => {
              if (
                url ===
                `/ucsschool/apis/bildungslogin/v1/user/${incomingVidisCoreRequest.sub}`
              ) {
                return buildSuccessfullResponse(
                  ucsResponseWithLicences[
                    'http://www.bildungslogin.de/licenses'
                  ],
                );
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
