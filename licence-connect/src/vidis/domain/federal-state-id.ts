export const simpleBundeslandIdentificationStrings = [
  'MV',
  'RP',
  'BW',
  'BY',
  'BE',
  'BB',
  'HB',
  'HH',
  'HE',
  'NI',
  'NW',
  'SL',
  'SN',
  'ST',
  'SH',
  'TH',
];
export type SimpleBundeslandAbbreviationString =
  (typeof simpleBundeslandIdentificationStrings)[number];

export const prefixedBundeslandAbbreviationStrings =
  simpleBundeslandIdentificationStrings.map((id: string) => 'DE-' + id);
export type PrefixedBundeslandAbbreviationString =
  (typeof prefixedBundeslandAbbreviationStrings)[number];

export const allBundeslandAbbreviations = [
  ...simpleBundeslandIdentificationStrings,
  ...prefixedBundeslandAbbreviationStrings,
];

export type BundeslandAbbreviation =
  (typeof allBundeslandAbbreviations)[number];
