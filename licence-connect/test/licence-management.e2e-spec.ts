import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { RequestFromVidisCore } from '@vidis/domain/request-from-vidis-core';
import { LicenceManagementConfigurationService } from '@licence-management/infrastructure/configuration/licence-management-configuration.service';
import { VidisConfigurationService } from '@vidis/infrastructure/configuration/vidis-configuration.service';

describe('LicenceManagementController (e2e)', () => {
  let app: INestApplication;

  const licenceManagerApiKey = 'licenceManager';
  const vidisApiKey = 'vidis';

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(LicenceManagementConfigurationService)
      .useValue({
        getConfiguration: () => {
          return {
            licenceManagementApiKey: licenceManagerApiKey,
          };
        },
      })
      .overrideProvider(VidisConfigurationService)
      .useValue({
        getConfiguration: () => {
          return {
            vidisApiKey: vidisApiKey,
          };
        },
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  const requestWithNonExistingUser: RequestFromVidisCore = {
    userId: '00000',
    clientId: 'sodix-editor-o',
    schulkennung: 'DE-RP-SN-51201',
    bundesland: 'DE-MV',
  };

  describe('v1/licence/ (POST)', () => {
    it('Add licences', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: [{ license_code: '1111' }],
        })
        .expect(201);
    });

    it('Reject licence request without licence_code', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: ['1111'],
        })
        .expect(400);
    });

    it('Reject licence request without studentId', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          licencesToAdd: [{ license_code: '1111' }],
        })
        .expect(400);
    });
  });
});
