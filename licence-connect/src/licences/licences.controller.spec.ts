import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { HttpService } from '@nestjs/axios';
import { UCSResponseWithLicences, incomingVidisCoreRequest } from './test_data';
import { AxiosResponse } from 'axios';
import { Licence } from './licence-types';
import { MVLicenceService } from './mv-licence.service';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  type MVStudent = {
    string: {
      id: string;
      first_name: string;
      last_name: string;
      licences: [Licence];
      context: {
        string: {
          licences: [Licence];
          classes: [{ licences: [Licence] }];
          workgroups: [{ licences: [Licence] }];
        };
      };
    };
  };

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
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((url: string) => {
              if (
                url ===
                `/ucsschool/apis/bildungslogin/v1/user/${incomingVidisCoreRequest.sub}`
              ) {
                return buildSuccessfullResponse(UCSResponseWithLicences);
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

  it('should answer with with no licences"', () => {
    expect(
      licencesController.getLicences({ body: incomingVidisCoreRequest }),
    ).toStrictEqual({
      hasLicence: false,
      licences: [],
    });
  });
});
