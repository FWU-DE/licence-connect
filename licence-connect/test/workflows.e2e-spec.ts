import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { RequestFromVidisCore } from '@vidis/domain/request-from-vidis-core';
import { LicenceManagementConfigurationService } from '@licence-management/infrastructure/configuration/licence-management-configuration.service';
import { VidisConfigurationService } from '@vidis/infrastructure/configuration/vidis-configuration.service';

describe('Workflows (e2e)', () => {
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

  fdescribe('v1/licence/ (POST)', () => {
    it('Add and fetch licences', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: [
            { license_code: '1111', educationalOffer: 'BA1' },
            { license_code: '1112', educationalOffer: 'BA1' },
          ],
        })
        .expect(201)
        .then(() => {
          return request(app.getHttpServer())
            .post('/v1/licences/request')
            .set({ 'X-API-KEY': vidisApiKey })
            .send(licenceRequest)
            .expect(200)
            .expect(
              `{"hasLicence":true,"licences":[{"educationalOfferId":"0","license_code":"1111"},{"educationalOfferId":"1","license_code":"1112"}]}`,
            );
        })
        .then(() => {
          return request(app.getHttpServer())
            .delete('/v1/licences/remove')
            .set({ 'X-API-KEY': licenceManagerApiKey })
            .send({
              studentId: 'Student1',
            })
            .expect(200);
        })
        .then(() => {
          return request(app.getHttpServer())
            .post('/v1/licences/request')
            .set({ 'X-API-KEY': vidisApiKey })
            .send(licenceRequest)
            .expect(200)
            .expect(`{"hasLicence":false,"licences":[]}`);
        });
    });
  });
});
