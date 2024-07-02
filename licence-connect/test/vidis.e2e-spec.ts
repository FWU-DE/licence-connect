import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { RequestFromVidisCore } from '@vidis/domain/request-from-vidis-core';
import { LicenceManagementConfigurationService } from '@licence-management/infrastructure/configuration/licence-management-configuration.service';
import { VidisConfigurationService } from '@vidis/infrastructure/configuration/vidis-configuration.service';

describe('LicenceController (e2e)', () => {
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

  describe('API KEY v1/licence (POST)', () => {
    describe('With valid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post(`/v1/licences/request?X-API-KEY=${vidisApiKey}`)
          .send(requestWithNonExistingUser)
          .expect(200)
          .expect(`{"hasLicence":false,"licences":[]}`);
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .set({ 'X-API-KEY': vidisApiKey })
          .send(requestWithNonExistingUser)
          .expect(200)
          .expect(`{"hasLicence":false,"licences":[]}`);
      });
    });

    describe('With invalid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post(`/v1/licences/request?X-API-KEY=${vidisApiKey}wrongApiKey`)
          .send(requestWithNonExistingUser)
          .expect(403);
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .set({ 'X-API-KEY': `${vidisApiKey}wrongApiKey` })
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });

    describe('Without api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });
  });
});
