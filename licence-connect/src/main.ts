import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { VersioningType } from '@nestjs/common';
import { WINSTON_MODULE_NEST_PROVIDER } from 'nest-winston';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.useLogger(app.get(WINSTON_MODULE_NEST_PROVIDER));
  app.enableVersioning({
    type: VersioningType.URI,
  });
  const config = new DocumentBuilder()
    .setTitle('Licence-connect API')
    .setDescription('This is the MVP API for licence connect.')
    .setVersion('0.0.1')
    .addTag('licences')
    .addTag('licenceManagement')
    .addTag('ucs')
    .addApiKey(
      { type: 'apiKey', name: 'X-API-KEY', in: 'header' },
      'VIDIS-Core',
    )
    .addApiKey(
      { type: 'apiKey', name: 'X-API-KEY', in: 'header' },
      'LicenceManagement',
    )
    .addApiKey({ type: 'apiKey', name: 'X-API-KEY', in: 'header' }, 'ucs')
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('swagger', app, document);
  await app.listen(3000);
}
bootstrap();
