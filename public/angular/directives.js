'use strict';

angular.module('sproutupApp').directive('ngConfirmClick',
    function(){
        return {
            scope: false,
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click',function (event) {
                    if ( window.confirm(msg) ) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }
);

angular.module('sproutupApp').directive('upFallbackSrc',
    function() {
        var missingDefault = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/4QBoRXhpZgAATU0AKgAAAAgABAEaAAUAAAABAAAAPgEbAAUAAAABAAAARgEoAAMAAAABAAIAAAExAAIAAAASAAAATgAAAAAAAABgAAAAAQAAAGAAAAABUGFpbnQuTkVUIHYzLjUuMTEA/9sAQwAEAgMDAwIEAwMDBAQEBAUJBgUFBQULCAgGCQ0LDQ0NCwwMDhAUEQ4PEw8MDBIYEhMVFhcXFw4RGRsZFhoUFhcW/9sAQwEEBAQFBQUKBgYKFg8MDxYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYW/8AAEQgB9AH0AwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A+ggKcBQBRQAUAZpQKUUAFOAxRSgUAIBThQBTqAAClAoApwFACU4CilAoAQCnYoxTsUAJilpcUtACYpaXFLQAmKXFLiloAbilxTsUuKAG0YNOowaAExS4FLilxQA2inYooAbRg07BpcGgBmDRg0/FGKAGYNFPxSYNADaKdg0UANxSYp+BSYoAbikwafikoAbikxT6TFADMUYp2KMUAR4oxT8UmKAGYpMU/FJigCPFJin4pMUAMpCKfSEUAMIppFSEU2gBhFIRTyKaRQAykIp9NoAaRmm08ikNADaQilooAbiilxRQAtKBSAZpwoABTqKUCgAApwFAFKKAACnAUU4CgBAKUClApQKAClApaUCgBMU7FGKdigBMUuKUClxQAmKXFLiloATFLS4paAG4NLinYpcUANpcUtLigBuKXAp2KKAG0YNOooATFGKWigBMUYpaKAExRilooATBpMGnUUANowKdRigBmKTBp+KMUAMxSYp9JigBlGKdijFADMUmKdiigBmKTFPxSYoAjxSYp+KTFADKQin00igBpFNxTyKQigCMikIp5FIRQAwim08ikNAEZGKQin00jFADTTaeRSEUANooooAdTqAMUoFAABTgKAKUUAAFOopwoAAKUCgCnAUAAFLQBTqADGKUClApQKAEApwFFOAoATFLRinAUAJilpcUuKAExS0uKWgBMUuKKKACijBpcUAJRTqMGgBMGjFOxRigBuKXAp2BRigBuBRgU6igBuBSYp9GBQAzFGKfgUmKAGYNFPxSYoAbRTsUmKAEpMUtFADcUYp1GKAI8UYp+KTFAEeKMU7FJigBmKQin0hFADKaRipCKaaAGEUhFPIpCKAI6aRUlNNADCKQjNPIppFADCKbUhFNNAEZGKQinn0ptADaKXFFACgU4CgU4UAAp1ApwFAAKUChRTgKAAClAzQBmnAUAFOFAFOAoAQClApQKWgApQKUClAoAQCnYop2KAExS0UUAFFLiloATFLS4paAExS4pcUuKAG0uKXBpcUANxS4FOxRigBtFOowaAG0U7BowaAG0U7BowaAG4oxTqMCgBmKTBp+KMUAMpMU+kxQAzBoxTsUYoAZikp+KSgBtGKXFJQA0ikxT6QigBlNIqTFNIoAaRTaeRSUARkUhFPIpCKAGEZptPIpCKAIzSEU+mmgBjCmkU8jFIwoAYRmmmnsKawoAZRTqKAHAUqihRTgKABR3pyikApwoAUClAoFOoAKcBQKcBQAAYpQKAKWgApwFFOAxQAgFOAoApaACiilAoAAKWgCnUAJilxS4paAExS4pcUuKAExS0uKWgBuDS4p2KMUAJgUU6igBuDS4pcGlwaAG4oxTsUYoAbijBp2KTBoAbg0U7BooAbgUmKfikxQAzFGKdg0YoAZikxT8UlADMUmKfikoAZikxTyKQigCOinUhFACYzTadRQAwimkU8jFIRQAymkYp5FJQAwimkU8jFIRQAwjNNNPYU0igBh9KaakIppoAjPFNIqQ02gBmKKWigBwp1ApVoAUU4UiinKKAFHFOApFFOUUACinAUgFOoAKdQBTgMUAAGKUCgCloAKKAM07FAAKUClAxSgUAJinUAU7FACYpaXFLQAYoxTsUUAJilpcUtADcGlxTsUYoATFGDTqMGgBMGjFOxRigBuKMU7FGKAG4oxTsUYoAZg0U/FJg0ANxSYp+KTFADKKdg0YoAZikp+KSgBmKSn4pKAIyKQin4pCKAGU0ipKaRigBhFJTyKQigBtNIxTqKAGEU0inkYpCKAGU008ikoAjIxSMKeaaaAGMKawp5ppFADGFNanmmmgBtFLiigBRThQKcKAAU4UCnCgAFOFApwoAKcBSKKcooAAKcooApaACgDNFOHpQAU4UClAoAAKcBQBSgZoAAKcBRinAUAIBS0YzTsUAJilpcUtACYpaXFLigBMUuKXFLigBuKXFLg0uKAG4pcCnYoxQA3AowKdRQA3ApMU+jAoAZijBp2KMUAMpMU/FGKAI8UYp+KTFADMUmKfikxQBHiinYpMUAMxSEU+kIoAYRTSKkIptADCKaRTzSEUAMIzTaeRSEUANppp1FAEZFIwp9NNADSKaacaRhQAw0009qa1ADDTTT29aa1ADKKdiigBVpy0lOoAVactIKcKAFWlFFOFAAKcKBThQAUUUq0AKKcBikUU5RQAAU4ChRSgZoAUClAoAp1ABSgUAUoFAAKcBRTgKAEApaUCloATFLilxS4oATFLS4pcUAJijFOxS4oAbiinUYNADaKdg0YNADaKdg0YNADcCkxT8UmKAGYNGKfikoAZikp+KSgBmKTFPxSEUAR4pCKeRSEUAMptSEZptADCKQinEYpCKAGEU01IRTaAIyKRhT6aaAGMKSnGmmgANNNOpGoAYabT2prUAMNNNPamtQAw0009qa1ADKKdRQAq05aSnCgBVpy0gp1ACrTlpKcKAFWloooAVaUUU4UAApwoFOFAAKcPSgU4CgApQKFFKBQAoFKKBTgKAClApQKUCgAApaAKdQAmKXFLilxQAYoxTsUYoATFLilxS0ANwaXBpaKAExRilooATFJg06igBtGKdRigBmKSn4pKAGYpKfikoAZikxT8UhFAEZFIRTzTSKAGEUhp5FNIoAZTSMVIaaaAGEUhFOpGFADDTTT2pretADDTae1NagBlFK1JQA00009qa1ADKaae1IaAIzTTUjUxqAGUU6igBVpy0gp1ACrTlpKcKAFWnLSU6gApVpKdQAq05aQU4UAKtOX1pBThQAq0o5opwoABTqBThQAU4CkApwFAABTgKAKUDNAABTqKcBQAgFLilApaADFFFFABRS4paAG4NLinYNGKAG4oxTsUYoAbijBp2KMUAMop2DRigBtFLikoATFJTqKAGEU0inkUhFADCKaakIzTaAGEU0inkU0jFADGFIRTiKRhQAwim09qa3rQAw001IaaaAIzTTUhppoAjNNNSNTWoAbTTTqRqAGU2ntTWoAYaaae1NagBlFOxRQALT1pq09elAAKetNWnrQAq0tA6UUAKtOWkFOFACrTlpBThQA5actNFOoAVactJTqAFWnLSU4UAKopQKKdQACnUCnCgAFKBQBS0AFFFOAoAQClxSgUuKAExS4p2KMUAJijFOxS4FADcCjAp1FADcCkxT6MUAMxSU/FJQAzFJT8UlADCKSnEUhFACUhFLRQAwikIp5FNYUAMNNp7CmtQAymmntSNQBGaaakamtQBHSNTmpDQAxqYakNNNAEZppp7U1qAGUUrUlADTTTT2prUAMpppx60jUAMooPWigB1Opq9acOtADqcKRactAC0L1opVoActOWkXpTh0oAVactIKcKAFWnLSU4UAKtOWkpwoAVactIKcKAFWnLSCnCgBVpyikFOoAKKKcKACnAUAUoFAABS4pQKWgAxRinYoxQAmKWnYooAbRg07BpcGgBlGKdg0UAMxSU/FJQAzFJTyKQigCMikIp9NIxQAwikp7CmsKAEppp1BoAjNNNSGmmgCM001IaaaAIzTTUhpjUAMNNp7U1qAGNTTT2prUAMNNp7U1qAGGm09qYetAA3SmNT6bQAxqa1PNNoAbRRRQAq05aRelOXpQAq09elNHSnUAFOFNFOHWgB1OHWkWnLQAq9aetNWnLQAq9aetNWnLQA5actIOlOFACrTlpBThQA5acKaKcKAHCiiigBVpy0gpwoAVRTgKQU4UAAFOFApwoAAKMUoFKBQAYowadijBoATFGKdijFADcGkp+KTFADMUlPxSUAMIpCKeRTWFADCKbTyKQigCM001IaaaAIzRTjTaAEam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I4rqFUKoVRgKMAVy0IuI33xrIrDoQDU/2i//AOek/wCtAHR0VzouL7/npN+tL599/wA9Jv1oA6Giue8++/56TfrR59//AM9Jv1oA6Giue8++/wCek360faL7/npN+tAHQ1n69aebD56D54xz7is37Rf/APPSb9aabi//AOek/wCtADdN/wCQjD/10H861PFH/IPT/rqP5GszTo5BqEJMbf6wZ+WtPxV/yD0/66j+RoA58000401qAGmm0rU1qAENNPSlamtQAlDdKKRqAGtTWpzUw9aAEamN1pxptADTTacelNbpQA09KaelK1NbpQAjdKY1OamtQA1qa3WnN1ph60ANPWiig9KAGnpTW6U5ulMagBKKKKAAU4dKYvSnLQA+nU1elOHSgAXrT1plOFAD1py0wdaetAD16U4dKYtOWgB46U4UxactAD6cKYtPFADh1p60wU4UAPWnLTKdQA6lWkooAetOWmU4UAPWnKaYKcDQA9TXQWeqWcdnFG0jbljUH5T1ArngaUGgDpf7Wsf+erf98Gl/tay/56N/3ya5oGnZoA6P+1bL/no3/fJo/tWy/wCejf8AfJrnc0uaAOi/tSz/AOejf98mj+1LP/no3/fJrncmjJoA6L+1LP8A56N/3yaT+1bL/no3/fJrnqM0AdD/AGrZf89G/wC+TSf2tY/89G/75Nc7mgmgDov7Wsf+ejf98GqOvX9tc2axwuSwkBOVI4wf8ayc00mgAJpppSaaxoAQ000rGmtQAhptK1JQAU005qY1ACU00rU1qAEPSmNTmprUANamtSt1ppoARqa1LTT1oARutMPWnU00ANpppx6U1ulADaRulLSNQA1qa1ObrTD1oAKKa3WigBVpy0wdadQA9actMHWnrQAtOHSm0q0APFOFMWnLQA8U5etNFOFADqcOtNpwoActPWmCnCgB605aYKdQA9actMp1AD1pabTqAFU0oNNpVNADwadTAaUGgCQGlBplOBoAeDS5pmaXNAD80uaZmjNAD80ZpuRRmgB2aTNJkUmaAFzRmm5ozQAE0hNJmkJoACaQmgmm0ABNNNBNNJoAKKKRjQAhpppWprUAIaaaVqa1ACGmmlamtQAhpp6UrU1qAEpp6UrU1qAEbpTGpzUxqAEamtSmm0AFNPWnU00ANNNpx6U00ANooooABTqYtOWgB4pwpi05aAH0Ui0tADh1py00U4UAPWnLTBThQA9actMp1AD1py0wU4UAPWnLTBTgaAHrTlpgpwNADwaUGmA05TQA+ikBpaAFBpwNMpQaAH5p2ajBp2aAH5pc0zNLmgB2TS5pmaXNADs0ZpuaM0AOzRmm5oyaAFzSZpM0maAFzSE0maTNACk00mjNNJoAUmkoooADTSaKaTQAU2gmkY0AJTaVjTWNACGmmlamtQAlNpWprUAIabStTWoAQ000rU1qAEPSm0rUlACNTWpW6000AI1Nalpp60AITRSUUAApwpq04UAOpwpopy0AOFOpopwoAVactMpwoAetOWmCnCgB605aYKcKAHCnCmilWgB4pwNMU05TQA8GnA0xTSg0ASA04GowadQA8GnA0wGlBoAfRTQadmgAzTs02igB2aXNMzS5oAfRk03NGTQA/NGaZk0ZNAD8mkyabk0ZoAdmkzSZpM0ALmkzSUUAFFFITQAuabQTTSaAAmkJoJppNAAxpCaCabQAE00mimk0ABppoNNNABTTStTWoAQ02lamtQAhptK1NagBKKKRqAENNbpStTWoAQ000rU1qAEopCaKAEWnrTBThQA9aUU0U6gBwp60wU4UAOpVpBRQA9acppgpwNADgacKYppymgB4p1MU05TQA8U4Go6dQA8GnKaYDSg0ASA0oNMBpQaAJAaUGmUuaAH5p2ajzTs0APzS0zNLmgB1FNyaXNAC0UZFFABmjNFFABRRRQAUUZFJmgBaM03NJmgBc0maTNJmgBaaTRSE0ABNITQTTaACmk0E0hNAATTWNBNITQAjGkNFNNAAaaaCaaaAA000E0jGgBDTaVqSgApppWprUAIabStTWoASm0rU1qAEopM0UAC05aYKcKAHrTlpgpwoAcKcKaKVaAHg06mLTlNAC04U2lU0APpwqMGnA0ASA04GowadQA9TSg00GlBoAfTgajBpwNADwaUGmg0oNAD6UGmZp2aAHZp2ajzTs0APzS5qPNLmgB+aXNMzS5oAfmjNMzS5FADs0Z96bkUZFADs0ZFNyKTNADs0ZpuaTJoAdSZpM0maAFzSZpM0maAFpCaSkJoAUmm0E00mgBSaaTQTSE0ABNNopCaAEJpGNDGmk0ADGmsaUmmk0AIxpCaCabQAUGimk0ABppoNNNABTaVqa1ACU00rU1qAEopM0UAIKcKYtOWgB4pwpi05aAHinUxTTlNADxThUYNOBoAkBopop1ACqacpplOBoAeDSg0wGnA0APBpwNRg06gB4NLTQaUGgB4NKDTKXNAD807NR5p2aAHZp2ajzS5oAfmlzTM0uaAH5oyabmjNAD80ZpuTRmgB2aM03NGaAHZozTc0ZoAdmkzTcmjNADs0mabmjNAC5pM0maTNAC5pM0maTNAC5puaM03NACk0lGabmgAJpCaCaaTQAE0hNBNNoACaaTQTSMaAEJoopCaABjTWNKTTSaAEY0hoppNAAaaaDTTQAGmmg000AFFITRQAlOFMU05TQA+nUxTSg0ASCnA1GDTgaAJAaVTTKdQA8GlBpimnA0APopoNOoAcDSg0ynA0AOBp1MBpQaAJAaUGmA0uaAH5p2ajzTs0AOpc03NLmgB2aXNMpc0APpc0zNGTQBJmimZpcigB2TS5NMzRk0APzRmmZNGTQA/NJk03JoyaAHZozTc0ZFAC5pMmkzSZoAdmkzSZpM0ALSZpKTNAC0maTNJmgBc00mjNNzQApNITSE0hNAATTSaCaQmgAJpKKCaAAmmk0U0mgAJptBNIxoAGNNY0pNNJoARjTWpSaaaAEY0jUU00AFFNooAAacDUYNOFAEgNKppgpwNAD1NOU0wGnA0AOBpwNMBpymgB9KDTAadQA8GlBpgNOBoAfRTQadQAoNKDTaAaAJM0oNMpQaAH5p2ajzTs0APzS5qPNOzQA7NLmmZpc0APoyabmjJoAfmjNNzS5FADs0ZpuaKAHZozTaKAHZozTaKAHZFJmkzSZoAdmkpM0mTQA6kzSZpM0ALmkzSUmaAFzSZpKTNAC5ppNGaQmgAJpCaCabQAE0UUhNACk02gmmk0ABNITQTSE0ABNNJoptABTSaCaQmgBCaaTSsaaxoAGNNY0MaQmgBM0UlFACKacppgNKDQA8GnA0xTTgaAH04VGDTgaAJAaVTTKcDQA8GlBpgNOBoAfSg0wGnUAOBp1MBpQaAJAaKaDS5oAWlzSUUAOzTs1HmnZoAdmlzTM0uaAH5pc0ylzQA/NGTTc0ZoAfmjNNyaM0APyKM0zNLkUAOzRmm5FGRQA7NGabkUZoAdkUmabmjNADsmkpuTRmgB2aTNNzSZoAdmkzSZpM0ALmkzSUmaAFzSZpKKACijNNJoAUmkJpCaQmgAJppNFITQApNNoJptAATTSaCaQmgAJprGgmkJoACaaTRTSaACm0E00mgAopM0UAJThTFNKKAJAaUGmU4GgB6mnA0wGlBoAeDTqYDSg0ASA0oNMBpQaAHg04GmA0uaAJAaM03NKDQA8GlBplKDQA/NLmmZpc0APopuaXNAC0uaSigB2aXNMoyaAH5pc0zNLkUAOyaXJpmaMmgB+aM03NGaAHZozTc0ZoAdmjNNzRmgB2aMmm5NJk0AOozTc0ZoAXNJmkzSUAOzSZpKKACiikzQAtJmkzSZoAXNNzRmm5oAUmkozTSaAFJpCaQmkJoAKaTQTSE0ABNNJoJpCaAAmm0U0mgAJpCaGNNJoAGNNY0pNNoAKKbmigABpVNMpwNADgadTAaUGgCQGnA1HTgaAHg0oNMBpwNAD804GowacDQA8GlzTAaUGgCQGlBpmaXNAD80uaZmlzQA+lzTM07NADqXNMpc0APzS5pmaXNAD80UzNLmgB1FJk0ZoAWijIozQAZNLk0lFAC5ozSUUALmkyaKKADJooozQAUUmaM0ALRTcmjNAC5pMmkzSZoAdmkzSZpM0ALmkzSUmaAFpM0lJmgBc03NGabmgBSaQmkzSZoACaQmkJpCaAFJptBNNJoACaQmgmmk0ABNITQTTaACmk0pNNY0AGaKSigAopFNLQA4GlBplOBoAcDTs0wGloAkBpQaYDSg0APzTgajBp1ADwaXNMBpQaAJM0uaZmlzQA/NLmmZpc0APzTs1HmlzQA/NLmmZpc0APoyabmlzQA7NLmmZpaAHZNLmmUuTQA7NLkUzNGaAH5ozTcijIoAdmjNNzRmgB2aM03NGRQA7NGRTcikzQA7NGTTc0mTQA7NGabRmgBc0lJmkyaAHZpM0maTNAC0maSkzQAtJmkzSZoAXNNzRmm5oAUmkozTc0AKTTSaCaQmgAppNFNJoAUmkJpCaSgApCaQmkJoACaSiigAoptFAAtKtFFAC0UUUAOpVoooAWnUUUAKtLRRQA6lWiigBacKKKACnUUUAFOoooAKcKKKACjJoooAdRRRQAZpc0UUALRRRQAUUUUAFFFFABRRRQAUUUUABpuTRRQAUUUUAITSUUUAFITRRQAlI1FFACUjUUUAJSMaKKAEptFFACNSUUUANpGoooASmmiigBGpKKKACm0UUAITRRRQB//Z";
        return {
            restrict: 'A',
            link: function(scope, element, attr) {
                element.on('error', function() {
                    element[0].src = attr.upFallbackSrc ? attr.upFallbackSrc : missingDefault;
                });
            }
        };
    }
);

angular.module('sproutupApp').directive('upBackgroundImage',
    function() {
        var missingDefault = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/4QBoRXhpZgAATU0AKgAAAAgABAEaAAUAAAABAAAAPgEbAAUAAAABAAAARgEoAAMAAAABAAIAAAExAAIAAAASAAAATgAAAAAAAABgAAAAAQAAAGAAAAABUGFpbnQuTkVUIHYzLjUuMTEA/9sAQwAEAgMDAwIEAwMDBAQEBAUJBgUFBQULCAgGCQ0LDQ0NCwwMDhAUEQ4PEw8MDBIYEhMVFhcXFw4RGRsZFhoUFhcW/9sAQwEEBAQFBQUKBgYKFg8MDxYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYW/8AAEQgB9AH0AwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A+ggKcBQBRQAUAZpQKUUAFOAxRSgUAIBThQBTqAAClAoApwFACU4CilAoAQCnYoxTsUAJilpcUtACYpaXFLQAmKXFLiloAbilxTsUuKAG0YNOowaAExS4FLilxQA2inYooAbRg07BpcGgBmDRg0/FGKAGYNFPxSYNADaKdg0UANxSYp+BSYoAbikwafikoAbikxT6TFADMUYp2KMUAR4oxT8UmKAGYpMU/FJigCPFJin4pMUAMpCKfSEUAMIppFSEU2gBhFIRTyKaRQAykIp9NoAaRmm08ikNADaQilooAbiilxRQAtKBSAZpwoABTqKUCgAApwFAFKKAACnAUU4CgBAKUClApQKAClApaUCgBMU7FGKdigBMUuKUClxQAmKXFLiloATFLS4paAG4NLinYpcUANpcUtLigBuKXAp2KKAG0YNOooATFGKWigBMUYpaKAExRilooATBpMGnUUANowKdRigBmKTBp+KMUAMxSYp9JigBlGKdijFADMUmKdiigBmKTFPxSYoAjxSYp+KTFADKQin00igBpFNxTyKQigCMikIp5FIRQAwim08ikNAEZGKQin00jFADTTaeRSEUANooooAdTqAMUoFAABTgKAKUUAAFOopwoAAKUCgCnAUAAFLQBTqADGKUClApQKAEApwFFOAoATFLRinAUAJilpcUuKAExS0uKWgBMUuKKKACijBpcUAJRTqMGgBMGjFOxRigBuKXAp2BRigBuBRgU6igBuBSYp9GBQAzFGKfgUmKAGYNFPxSYoAbRTsUmKAEpMUtFADcUYp1GKAI8UYp+KTFAEeKMU7FJigBmKQin0hFADKaRipCKaaAGEUhFPIpCKAI6aRUlNNADCKQjNPIppFADCKbUhFNNAEZGKQinn0ptADaKXFFACgU4CgU4UAAp1ApwFAAKUChRTgKAAClAzQBmnAUAFOFAFOAoAQClApQKWgApQKUClAoAQCnYop2KAExS0UUAFFLiloATFLS4paAExS4pcUuKAG0uKXBpcUANxS4FOxRigBtFOowaAG0U7BowaAG0U7BowaAG4oxTqMCgBmKTBp+KMUAMpMU+kxQAzBoxTsUYoAZikp+KSgBtGKXFJQA0ikxT6QigBlNIqTFNIoAaRTaeRSUARkUhFPIpCKAGEZptPIpCKAIzSEU+mmgBjCmkU8jFIwoAYRmmmnsKawoAZRTqKAHAUqihRTgKABR3pyikApwoAUClAoFOoAKcBQKcBQAAYpQKAKWgApwFFOAxQAgFOAoApaACiilAoAAKWgCnUAJilxS4paAExS4pcUuKAExS0uKWgBuDS4p2KMUAJgUU6igBuDS4pcGlwaAG4oxTsUYoAbijBp2KTBoAbg0U7BooAbgUmKfikxQAzFGKdg0YoAZikxT8UlADMUmKfikoAZikxTyKQigCOinUhFACYzTadRQAwimkU8jFIRQAymkYp5FJQAwimkU8jFIRQAwjNNNPYU0igBh9KaakIppoAjPFNIqQ02gBmKKWigBwp1ApVoAUU4UiinKKAFHFOApFFOUUACinAUgFOoAKdQBTgMUAAGKUCgCloAKKAM07FAAKUClAxSgUAJinUAU7FACYpaXFLQAYoxTsUUAJilpcUtADcGlxTsUYoATFGDTqMGgBMGjFOxRigBuKMU7FGKAG4oxTsUYoAZg0U/FJg0ANxSYp+KTFADKKdg0YoAZikp+KSgBmKSn4pKAIyKQin4pCKAGU0ipKaRigBhFJTyKQigBtNIxTqKAGEU0inkYpCKAGU008ikoAjIxSMKeaaaAGMKawp5ppFADGFNanmmmgBtFLiigBRThQKcKAAU4UCnCgAFOFApwoAKcBSKKcooAAKcooApaACgDNFOHpQAU4UClAoAAKcBQBSgZoAAKcBRinAUAIBS0YzTsUAJilpcUtACYpaXFLigBMUuKXFLigBuKXFLg0uKAG4pcCnYoxQA3AowKdRQA3ApMU+jAoAZijBp2KMUAMpMU/FGKAI8UYp+KTFADMUmKfikxQBHiinYpMUAMxSEU+kIoAYRTSKkIptADCKaRTzSEUAMIzTaeRSEUANppp1FAEZFIwp9NNADSKaacaRhQAw0009qa1ADDTTT29aa1ADKKdiigBVpy0lOoAVactIKcKAFWlFFOFAAKcKBThQAUUUq0AKKcBikUU5RQAAU4ChRSgZoAUClAoAp1ABSgUAUoFAAKcBRTgKAEApaUCloATFLilxS4oATFLS4pcUAJijFOxS4oAbiinUYNADaKdg0YNADaKdg0YNADcCkxT8UmKAGYNGKfikoAZikp+KSgBmKTFPxSEUAR4pCKeRSEUAMptSEZptADCKQinEYpCKAGEU01IRTaAIyKRhT6aaAGMKSnGmmgANNNOpGoAYabT2prUAMNNNPamtQAw0009qa1ADKKdRQAq05aSnCgBVpy0gp1ACrTlpKcKAFWloooAVaUUU4UAApwoFOFAAKcPSgU4CgApQKFFKBQAoFKKBTgKAClApQKUCgAApaAKdQAmKXFLilxQAYoxTsUYoATFLilxS0ANwaXBpaKAExRilooATFJg06igBtGKdRigBmKSn4pKAGYpKfikoAZikxT8UhFAEZFIRTzTSKAGEUhp5FNIoAZTSMVIaaaAGEUhFOpGFADDTTT2pretADDTae1NagBlFK1JQA00009qa1ADKaae1IaAIzTTUjUxqAGUU6igBVpy0gp1ACrTlpKcKAFWnLSU6gApVpKdQAq05aQU4UAKtOX1pBThQAq0o5opwoABTqBThQAU4CkApwFAABTgKAKUDNAABTqKcBQAgFLilApaADFFFFABRS4paAG4NLinYNGKAG4oxTsUYoAbijBp2KMUAMop2DRigBtFLikoATFJTqKAGEU0inkUhFADCKaakIzTaAGEU0inkU0jFADGFIRTiKRhQAwim09qa3rQAw001IaaaAIzTTUhppoAjNNNSNTWoAbTTTqRqAGU2ntTWoAYaaae1NagBlFOxRQALT1pq09elAAKetNWnrQAq0tA6UUAKtOWkFOFACrTlpBThQA5actNFOoAVactJTqAFWnLSU4UAKopQKKdQACnUCnCgAFKBQBS0AFFFOAoAQClxSgUuKAExS4p2KMUAJijFOxS4FADcCjAp1FADcCkxT6MUAMxSU/FJQAzFJT8UlADCKSnEUhFACUhFLRQAwikIp5FNYUAMNNp7CmtQAymmntSNQBGaaakamtQBHSNTmpDQAxqYakNNNAEZppp7U1qAGUUrUlADTTTT2prUAMpppx60jUAMooPWigB1Opq9acOtADqcKRactAC0L1opVoActOWkXpTh0oAVactIKcKAFWnLSU4UAKtOWkpwoAVactIKcKAFWnLSCnCgBVpyikFOoAKKKcKACnAUAUoFAABS4pQKWgAxRinYoxQAmKWnYooAbRg07BpcGgBlGKdg0UAMxSU/FJQAzFJTyKQigCMikIp9NIxQAwikp7CmsKAEppp1BoAjNNNSGmmgCM001IaaaAIzTTUhpjUAMNNp7U1qAGNTTT2prUAMNNp7U1qAGGm09qYetAA3SmNT6bQAxqa1PNNoAbRRRQAq05aRelOXpQAq09elNHSnUAFOFNFOHWgB1OHWkWnLQAq9aetNWnLQAq9aetNWnLQA5actIOlOFACrTlpBThQA5acKaKcKAHCiiigBVpy0gpwoAVRTgKQU4UAAFOFApwoAAKMUoFKBQAYowadijBoATFGKdijFADcGkp+KTFADMUlPxSUAMIpCKeRTWFADCKbTyKQigCM001IaaaAIzRTjTaAEam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I4rqFUKoVRgKMAVy0IuI33xrIrDoQDU/2i//AOek/wCtAHR0VzouL7/npN+tL599/wA9Jv1oA6Giue8++/56TfrR59//AM9Jv1oA6Giue8++/wCek360faL7/npN+tAHQ1n69aebD56D54xz7is37Rf/APPSb9aabi//AOek/wCtADdN/wCQjD/10H861PFH/IPT/rqP5GszTo5BqEJMbf6wZ+WtPxV/yD0/66j+RoA58000401qAGmm0rU1qAENNPSlamtQAlDdKKRqAGtTWpzUw9aAEamN1pxptADTTacelNbpQA09KaelK1NbpQAjdKY1OamtQA1qa3WnN1ph60ANPWiig9KAGnpTW6U5ulMagBKKKKAAU4dKYvSnLQA+nU1elOHSgAXrT1plOFAD1py0wdaetAD16U4dKYtOWgB46U4UxactAD6cKYtPFADh1p60wU4UAPWnLTKdQA6lWkooAetOWmU4UAPWnKaYKcDQA9TXQWeqWcdnFG0jbljUH5T1ArngaUGgDpf7Wsf+erf98Gl/tay/56N/3ya5oGnZoA6P+1bL/no3/fJo/tWy/wCejf8AfJrnc0uaAOi/tSz/AOejf98mj+1LP/no3/fJrncmjJoA6L+1LP8A56N/3yaT+1bL/no3/fJrnqM0AdD/AGrZf89G/wC+TSf2tY/89G/75Nc7mgmgDov7Wsf+ejf98GqOvX9tc2axwuSwkBOVI4wf8ayc00mgAJpppSaaxoAQ000rGmtQAhptK1JQAU005qY1ACU00rU1qAEPSmNTmprUANamtSt1ppoARqa1LTT1oARutMPWnU00ANpppx6U1ulADaRulLSNQA1qa1ObrTD1oAKKa3WigBVpy0wdadQA9actMHWnrQAtOHSm0q0APFOFMWnLQA8U5etNFOFADqcOtNpwoActPWmCnCgB605aYKdQA9actMp1AD1pabTqAFU0oNNpVNADwadTAaUGgCQGlBplOBoAeDS5pmaXNAD80uaZmjNAD80ZpuRRmgB2aTNJkUmaAFzRmm5ozQAE0hNJmkJoACaQmgmm0ABNNNBNNJoAKKKRjQAhpppWprUAIaaaVqa1ACGmmlamtQAhpp6UrU1qAEpp6UrU1qAEbpTGpzUxqAEamtSmm0AFNPWnU00ANNNpx6U00ANooooABTqYtOWgB4pwpi05aAH0Ui0t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";
        return {
            restrict: 'A',
            link: function(scope, element, attr) {

                attr.$observe('upBackgroundImage', function (upBackgroundImage){
                    var url = attr.upBackgroundImage
                    if(!attr.upBackgroundImage){
                        url = attr.upFallbackImage ? attr.upFallbackImage : missingDefault;
                    }
                    element.css({
                        'background-image': 'url(' + url + attr.upBackgroundAttr + ')'
                    });
                });

                var url = attr.upBackgroundImage
                if(!attr.upBackgroundImage){
                    url = attr.upFallbackImage ? attr.upFallbackImage : missingDefault;
                }
                element.css({
                    'background-image': 'url(' + url + attr.upBackgroundAttr +')'
                });
            }
        };
    }
);

angular.module('sproutupApp').directive('upProductAbout',
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about.html',
            replace: true,
            restrict: "E",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
);

angular.module('sproutupApp').directive('upProductAboutTemplate1',
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-template-1.html',
            replace: true,
            restrict: "E",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
);

angular.module('sproutupApp').directive('upProductAboutTemplate2',
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-template-2.html',
            replace: true,
            restrict: "E",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
);

angular.module('sproutupApp').directive('upProductAboutDescription', [
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-description.html',
            replace: false,
            restrict: "EA",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductAboutFeatures', [
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-features.html',
            replace: false,
            restrict: "EA",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductAboutMission', [
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-mission.html',
            replace: false,
            restrict: "EA",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
]);

angular.module('sproutupApp').directive('upSameHeight', ['Utils', '$window', '$timeout',
    function(utils, $window, $timeout){
        return{
            replace: false,
            restrict: "A",
            scope:{
            },
            link: function(scope, element, attrs){
                scope.setHeight = function(){
                    //var children = element.children();
                    var children = element.find(".watch");
                    var heights = [];
                    for(var i=0;i<children.length;i++){
                        angular.element(children[i]).height('auto');
                    }
                    for(var i=0;i<children.length;i++){
                        console.log("height ",angular.element(children[i]).height());
                        heights.push(angular.element(children[i]).height());
                    }

                    //var heights = element.children().map(function(child) {
                    //    return angular.element(child).height();
                    //});

                    //var heightsx = element.find(".watch").map(function(item) {
                    //        return item.height;
                    //    });

                    //var pageOne = angular.element(element.children()[0]);

                    var maxHeight = Math.max.apply(null, heights);

                    if(maxHeight == 0 || !isFinite(maxHeight)) {
                        //console.log("up-same-height try again...");
                        // wait and try again
                        $timeout(
                            function () {
                                scope.setHeight();
                            },
                            1000,
                            true,
                            scope
                        );
                    }
                    else{
                        element.find(".watch").height(maxHeight);
                        //console.log("up-same-height ", heights, maxHeight, element.height(), element[0].offsetHeight, pageOne.height());
                        //element.find(".watch").height(maxHeight);
                        //for(var i=0;i<children.length;i++){
                        //    //console.log("height ",angular.element(children[i]).height());
                        //    angular.element(children[i]).height(maxHeight);
                        //}
                    }
                };

                $timeout(function () {
                    scope.setHeight();
                }, 100);

                $timeout(function () {
                    scope.setHeight();
                }, 500);

                $timeout(function () {
                    scope.setHeight();
                }, 1000);

                var w = angular.element($window);

                scope.getWindowDimensions = function () {
                    return { 'h': w.height(), 'w': w.width() };
                };

                scope.$watch(scope.getWindowDimensions, function (newValue, oldValue) {
                    scope.windowHeight = newValue.h;
                    scope.windowWidth = newValue.w;
                    scope.setHeight();
                }, true);

                w.bind('resize', function () {
                    scope.$apply();
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductAboutStory', [
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-story.html',
            replace: false,
            restrict: "EA",
            scope:{
                product: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductAboutTeam', [
    function(){
        return{
            templateUrl: 'assets/templates/up-product-about-team.html',
            replace: false,
            restrict: "EA",
            scope:{
                product: "=",
                justCreator: "="
            },
            link: function(scope, element, attrs){
            }
        }
    }
]);

angular.module('sproutupApp').directive('upTwitterShare',['$location',
    function($location){
        return{
            templateUrl: 'assets/templates/up-twitter-share.html',
            replace: true,
            restrict: "E",
            scope:{
                hash: "=",
                product: "=",
                name: "="
            },
            link: function(scope, element, attrs){
                attrs.$observe('hash', function (hash) {
                    scope.url = "url=" + window.encodeURIComponent("http://sproutup.co" + $location.path() + '#' + scope.hash);
                    scope.text = "text=" + window.encodeURIComponent("Check out what " + scope.name + " said about " + scope.product.productName + " @sproutupco");
                    if(scope.product.twitterUserName != null){
                        scope.text += " @" + scope.product.twitterUserName;
                    }
                    scope.path = "https://twitter.com/intent/tweet?" + scope.url + "&" + scope.text;
                });
            }
        }
    }
]);


angular.module('sproutupApp').directive('upTwitterTweetButton',['$location', '$timeout',
    function($location, $timeout){
        return{
            restrict: "A",
            scope:{
                text: "@"
            },
            link: function(scope, element, attrs){
                scope.twttrReady = false;
                scope.textReady = false;

                twttr.ready(
                    function (twttr) {
                        console.log("tweet button - twttr ready");
                        scope.twttrReady = true;
                        scope.render();
                    }
                );

                attrs.$observe('text', function() {
                    console.log("twttr button: text observe", scope.text);
                    if(scope.twttrReady){
                        scope.textReady = true;
                    }
                    scope.render();
                });

                scope.render = function() {
                    if(scope.twttrReady == false){
                        console.log("twttr button render : twitter not ready yet");
                        return;
                    }

                    if(scope.textReady == false){
                        console.log("twttr button render : text not ready yet");
                        return;
                    }

                    console.log("twttr button render");

                    scope.url = "http://sproutup.co" + $location.path();
                    scope.intent = "https://twitter.com/intent/tweet?text=" + encodeURIComponent(scope.text) + "&via=sproutupco&url=" + scope.url;

                    attrs.$set('href', scope.intent);

//                    twttr.widgets.load();
                };
            }
        }
    }
]);

angular.module('sproutupApp').directive('upTwitterTweet', ['TwitterService','$timeout',
    function(twitterService, $timeout){
        return{
            templateUrl: 'assets/templates/up-twitter-tweet.html',
            restrict: "E",
            scope:{
                productId: "=",
                api: "@"
            },
            link: function(scope, element, attrs){
                scope.twttrReady = false;

                scope.user_timeline = function(){
                    console.log("twttr render user_timeline");
                    twitterService.user_timeline(scope.productId).then(
                        function(data){
                            var arrayLength = data.length;
                            for (var i = 0; i < arrayLength; i++) {
                                element.append("<div id='tweet"+i+"'</div>");
                                twttr.widgets.createTweet(
                                    data[i].id_str,
                                    document.getElementById('tweet'+i),
                                    {
//                                    cards: 'hidden'
//                                    theme: 'dark'
                                    }).then(function (el) {
                                    });
                            }
                        },
                        function(error){

                        }
                    );
                };

                scope.search = function() {
                    console.log("twttr render search")
                    twitterService.search(scope.productId).then(
                        function(data){
                            var arrayLength = data.statuses.length;
                            for (var i = 0; i < arrayLength; i++) {
                                element.append("<div id='tweet"+i+"'</div>");
                                twttr.widgets.createTweet(
                                    data.statuses[i].id_str,
                                    document.getElementById('tweet'+i),
                                    {
//                                    cards: 'hidden'
//                                    theme: 'dark'
                                    }).then(function (el) {
                                    });
                            }
                        },
                        function(error){

                        }
                    );
                };

                scope.render = function() {
                    //scope.prodId = scope.productId || -1;
                    if(scope.twttrReady == false){
                        console.log("twttr render : twitter not ready yet");
                        return;
                    }

                    if(scope.productId === undefined){
                        console.log("twttr render : product id type = ", typeof scope.productId);
                        // wait and try again
                        $timeout(
                            function(){
                                console.log("twttr timeout : product id type = ", typeof scope.productId);
                                scope.render();
                            },
                            1000,
                            true,
                            scope
                        );

                        return;
                    }

                    if(scope.api == "statuses/user_timeline"){
                        scope.user_timeline();
                    }
                    else{
                        scope.search();
                    }
                };

                attrs.$observe('productId', function() {
//                    console.log("twttr : observe product id type = ", typeof scope.productId);
                    scope.render();
                });

                twttr.ready(
                    function (twttr) {
                        console.log("twttr ready");
                        scope.twttrReady = true;
                        scope.render();
                    }
                );
            }
        }
    }
]);


angular.module('sproutupApp').directive('upTwitterTimeline', ['TwitterService',
    function(twitterService){
        return{
            templateUrl: 'assets/templates/up-twitter-timeline.html',
            restrict: "E",
            scope:{
                productId: "="
            },
            link: function(scope, element, attrs){
                twitterService.show(scope.productId).then(
                    function(data){
                        twttr.widgets.createTimeline(
                            '585242050129440768',
                            document.getElementById('timeline'),
                            {
                                width: '604',
                                height: '700',
                                userId: data.id_str,
                                related: 'twitterdev,twitterapi'
                            }).then(function (el) {
                                console.log("Embedded a timeline.")
                            });
                    },
                    function(error){

                    }
                );
            }
        }
    }
]);

// <div class="fb-post" data-href="https://www.facebook.com/belledstech/posts/360314004151782" data-width="500"><div class="fb-xfbml-parse-ignore"><blockquote cite="https://www.facebook.com/belledstech/posts/360314004151782"><p>Awesome interface for Nook to control the Q developed by Ronald!</p>Posted by <a href="https://www.facebook.com/belledstech">Belleds</a> on <a href="https://www.facebook.com/belledstech/posts/360314004151782">Tuesday, March 17, 2015</a></blockquote></div></div>

angular.module('sproutupApp').directive ('upFacebookPost', ['Facebook', 'FacebookService', '$log', '$parse',
    function(facebook, facebookService, $log, $parse) {
        return{
            template: "<div ng-repeat='post in posts | limitTo:3' class='fb-post' " +
            "data-href='https://www.facebook.com/belledstech/posts/{{post.post_id}}'>{{post.post_id}}</div>",
            restrict: 'E',
            scope: {
                product: "="
            },
            link: function(scope, element, attrs){
                scope.$watch(function() {
                    // This is for convenience, to notify if Facebook is loaded and ready to go.
                    return facebook.isReady() && scope.product != undefined;
                }, function(newVal) {
                    if(newVal){
                        $log.debug("facebook > ready ", newVal);
                        // You might want to use this to disable/show/hide buttons and else
                        scope.facebookReady = true;

                        $log.debug("facebook > productId = ", scope.product);

                        facebookService.get(scope.product).then(
                            function(data){
                                $log.debug("facebook > received post data");
                                if (typeof data.data != 'undefined'){
                                    data.data.forEach(
                                        function(post){
                                            post.post_id = post.id.split("_")[1];
                                        }
                                    );
                                }
                                scope.posts = data.data;
                                facebook.parseXFBML();
                            },
                            function(error){

                            }
                        );
                    }
                    else{
                        $log.debug("facebook > not ready ", newVal);
                    }

                    //facebook.login(function(){
                    //
                    //    facebook.api('/me', function(response) {
                    //        console.log(JSON.stringify(response));
                    //    });
                    //
                    //}, {scope: 'publish_actions'});

                    //facebook.api('/me/feed', 'post', {message: 'Hello, world!'});

                });

                scope.login = function() {
                    // From now on you can use the Facebook service just as Facebook api says
                    facebook.login(function(response) {
                        // Do something with response.
                    });
                };
                //FB.login(function(){}, {scope: 'read_stream'});
                //read_stream
                //facebook.api();
                //facebook.login();

            }
        };
    }
]);

/*
    Facebook share button
*/
angular.module('sproutupApp').directive ('upFacebookLikeShare', ['Facebook', '$location', '$log', '$parse',
    function(facebook, $location, $log, $parse) {
        return{
            restrict: 'A',
            scope: {
            },
            link: function(scope, element, attrs){
                scope.$watch(function() {
                    // This is for convenience, to notify if Facebook is loaded and ready to go.
                    return facebook.isReady();
                }, function(newVal) {
                    if(newVal){
                        $log.debug("facebook > ready ", newVal);
                        scope.path = "http://www.sproutup.co" + $location.path();
                        // You might want to use this to disable/show/hide buttons and else
                        scope.facebookReady = true;
                    }
                    else{
                        $log.debug("facebook > not ready ", newVal);
                    }
                });

                element.bind('click', function() {
                    console.log("facebook share: ", scope.path );
                    FB.ui(
                     {
                      method: 'share',
                      href: scope.path
                    }, function(response){});
                });
            }
        };
    }
]);


/*
    Search icon directive
    Listens for stage changes and sets the search icon accordingly.
    After search return to previous state.
 */
angular.module('sproutupApp').directive('upSearchIcon', [ '$rootScope', '$log', '$state',
    function($rootScope, $log, $state){
        return{
            restrict: 'A',
            scope: {},
            link: function(scope, element, attrs) {
                var previous_state = "home";
                var state =  $state.current.name;

                update();

                function update(){
                    if(state=="search"){
                        element.find("i").addClass('active');
                    }
                    else{
                        element.find("i").removeClass('active');
                    }
                }

                element.bind('click', function() {
                    $log.debug("search - click()" + state + " " + previous_state);
                    if(state=="search") {
                        $state.go(previous_state);
                    }
                    else{
                        $state.go("search");
                    }
                });

                scope.$on('$stateChangeSuccess',
                    function (ev, to, toParams, from, fromParams) {
                        //assign the "from" parameter to something
                        console.log('search state change ' + from.name);
                        state =  $state.current.name;
                        update();
                    }
                );
            }
        }
    }
]);

/*
  General alert message handler
  Usage:
  broadcast a message like this and it will be flashed
  $rootScope.$broadcast('alert:success', {
    message: 'insert message here'
  });
 */
angular.module('sproutupApp').directive('upAlert', ['$timeout',
    function($timeout) {
        return{
            restrict: 'EA',
            replace: true,
            template: '<div ng-show="state.show" class="col-sm-12 alert" ng-class="state.status" role="alert">{{state.message}}</div>',
            scope: {},
            link: function(scope, element, attrs){
                scope.state = {
                    message: "",
                    status: "",
                    show: false
                };

                scope.$on('alert:success', function (event, args) {
                    scope.state.message = args.message;
                    scope.state.status = "success";
                    scope.state.show = true;

                    $timeout(
                        function(){
                            scope.state.message = "";
                            scope.state.status = "";
                            scope.state.show = false;
                        },
                        3000,
                        true,
                        scope
                    );
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upEarlyAccessRequest', ['EarlyAccessRequestService', '$log', '$rootScope',
    function(earlyAccessRequestService, $log, $rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'assets/templates/up-early-access-request.html',
            scope: {},
            controller: function( $scope, $element, $attrs, $transclude ) {
                // Controller code goes here.
                $scope.addRequest = function () {
                    $log.debug("earlyAccessRequestService > add request");
                    earlyAccessRequestService.add($scope.newRequest).then(
                        function(data){
                            $rootScope.$broadcast('alert:success', {
                                message: 'Thank you! Your request is received. Will get back to you shortly!'
                            });
                            $scope.newRequest.email = "";
                            $scope.newRequest.name = "";
                            $scope.newRequest.productUrl = "";
                            $scope.request.$setPristine();
                            $scope.request.$setUntouched();
                        }
                    );
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductSuggest', ['ProductSuggestionService', '$log', '$rootScope',
    function(productSuggestionService, $log, $rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'assets/templates/up-product-suggest.html',
            scope: {},
            controller: function( $scope, $element, $attrs, $transclude ) {
                // Controller code goes here.
                $scope.addSuggestion = function () {
                    $log.debug("productSuggestionService > add suggestion");
                    productSuggestionService.add($scope.newSuggestion).then(
                        function(data){
                            $scope.newSuggestion.email = "";
                            $scope.newSuggestion.productName = "";
                            $scope.newSuggestion.productUrl = "";
                            $scope.suggestion.$setPristine();
                            $scope.suggestion.$setUntouched();
                            $rootScope.$broadcast('alert:success', {
                                message: "Thanks for your suggestion. We will check it out!"
                             });
                        }
                    );
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upSlideable', function () {
    return {
        restrict:'AC',
        compile: function (element, attr) {
            // wrap tag
            var contents = element.html();
            element.html('<div class="slideable_content" style="margin:0 !important; padding:0 !important" >' + contents + '</div>');

            return function postLink(scope, element, attrs) {
                // default properties
                attrs.duration = (!attrs.duration) ? '0.4s' : attrs.duration;
                attrs.easing = (!attrs.easing) ? 'ease-in-out' : attrs.easing;
                element.css({
                    'overflow': 'hidden',
                    'height': '0px',
                    'transitionProperty': 'height',
                    'transitionDuration': attrs.duration,
                    'transitionTimingFunction': attrs.easing
                });
            };
        }
    };
});

angular.module('sproutupApp').directive('upSlideableToggle', ['$rootScope',
    function($rootScope) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var target, content;
                console.log("slideable toggle - init");

                attrs.expanded = false;

                element.bind('click', function() {
                    console.log("slideable toggle - click event");
                    if (!target) target = document.querySelector(attrs.upSlideableToggle);
                    if (!content) content = target.querySelector('.slideable_content');

                    if(!attrs.expanded) {
                        content.style.border = '1px solid rgba(0,0,0,0)';
                        var y = content.clientHeight;
                        content.style.border = 0;
                        target.style.height = y + 'px';
                    } else {
                        target.style.height = '0px';
                    }
                    attrs.expanded = !attrs.expanded;
                });

                $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
                    console.log("product suggest > state changed event");
                    if (!target) target = document.querySelector(attrs.upSlideableToggle);
                    if (!content) content = target.querySelector('.slideable_content');
                    if(attrs.expanded) {
                        target.style.height = '0px';
                        attrs.expanded = !attrs.expanded;
                    }
                })
            }
        };
    }
]);

// angular.module('sproutupApp').directive('upLike', ['LikesService', 'AuthService', '$timeout',
//     function (likesService, authService, $timeout) {
//         return {
//             restrict: 'E',
//             replace: true,
//             scope: {
//                 likes: '=',
//                 id: '=upId',
//                 type: '@upType'
//             },
//             templateUrl: 'assets/templates/up-like.html',
//             link: function (scope, element, attrs) {
//                 attrs.$observe('likes', function (likes) {
//                     didIlikeItAlready();
//                 });

//                 scope.$watch(function () {
//                         return authService.loggedIn();
//                     },
//                     function(newVal, oldVal) {
//                         didIlikeItAlready();
//                     }, true);

//                 function didIlikeItAlready() {
//                     if(authService.loggedIn()) {
//                         var userid = authService.m.user.id;
//                         if (scope.likes == undefined) {
//                             return false;
//                         }
//                         for (var i = 0; i < scope.likes.length; i++) {
//                             if (scope.likes[i].id == userid) {
//                                 scope.upvoted = true;
//                                 return true;
//                             }
//                         }
//                     }
//                     scope.upvoted = false;
//                     return false;
//                 }

//                 element.on('click', function () {
//                     console.log("#########################");
//                     console.log("up-like > clicked id/type: " + scope.id + "/" + scope.type);
//                     console.log("up-like > is-logged-in: " + authService.loggedIn());

//                     if(!authService.loggedIn()){
//                         scope.$emit('LoginEvent', {
//                             someProp: 'Sending you an Object!' // send whatever you want
//                         });
//                         return;
//                     }

//                     if(scope.likes == undefined){
//                         scope.likes = [];
//                     }

//                     console.log("user.id: " + authService.m.user.id);

//                     if(didIlikeItAlready()==false){
//                         likesService.addLike(scope.id, scope.type, authService.m.user.id).then(
//                             function(data) {
//                                 console.log("liked it: " + scope.id);
//                                 scope.likes.push(data);
//                                 scope.upvoted = true;
//                             }, function(reason) {
//                                 console.log('up-files failed: ' + reason);
//                             }
//                         );
//                     };

//                 });
//             }
//         }
//     }
// ]);

angular.module('sproutupApp').directive('upAvatarUpload', ['FileService', 'AuthService', '$timeout',
    function (fileService, authService, $timeout) {
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                user: '='
            },
            templateUrl: 'assets/templates/up-avatar-upload.html',
            link: function (scope, element, attrs) {
                scope.myself = false;

                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-video - file changed: ", file);
                    }
                });

                scope.$watch('user', function(newValue, oldValue) {
                //attrs.$observe('user', function (user) {
                    if (scope.user) {
                        if(scope.user.id == authService.m.user.id){
                            scope.myself = true;
                        }
                    }
                });

                $timeout(function () {
                }, 0);

                scope.$watch(function(scope) { return scope.files },
                function(files) {
                    console.log("watch files...");
                    if (files != null) {
                        for (var i = 0; i < files.length; i++) {
                            scope.errorMsg = null;
                            (function(file) {
                                console.log("generate thumbnail");
                                generateThumb(file);
                            })(files[i]);
                        }
                    }
                });

                function generateThumb(file) {
                    if (file != null) {
                        console.log("set thumbnail...found file of type: " + file.type);
                        if (file.type.indexOf('image') > -1) {
                            console.log("set thumbnail...reader supported");
                            $timeout(function() {
                                console.log("set thumbnail...timeout entered");
                                var fileReader = new FileReader();
                                fileReader.readAsDataURL(file);
                                fileReader.onload = function(e) {
                                    $timeout(function() {
                                        console.log("set thumbnail url");
                                        file.dataUrl = e.target.result;
                                    });
                                }
                            });
                        }
                        else
                        if (file.type.indexOf('video') > -1) {
                            console.log("set thumbnail...reader supported");
                            file.dataUrl = "/assets/images/video-thumbnail.png";
                        }
                    }
                };

                scope.upload = function (files, message, refId, refType) {
                    console.log("upload: ", refId, refType, files);
                    fileService.authenticate(scope.files[0], message, refId, refType).then(
                        function (result) {
                            console.log("upload auth returned");
                            scope.files[0].progress = 50;

                            fileService.upload(result.file, result.data).then(
                                function (result) {
                                    // verify that upload to s3 is done
                                    fileService.verify(result.file, result.uuid).then(
                                        function (result) {
                                            result.file.progress = 100;
                                            fileService.addAvatar(result.file, result.uuid).then(
                                                function (result) {
                                                    result.file.progress = 100;
                                                    authService.getUser().then(
                                                        function(result){
                                                            scope.files[0].dataUrl = null;
                                                            scope.files = null;
                                                            scope.user.avatarUrl = result.avatarUrl;
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                    )
                                },
                                function (error) {
                                    // todo handle error
                                },
                                function (result) {
                                    console.log("progress: ", result);
                                    scope.files[0].progress = (50 + (result.progress / 2)).toFixed(1);
                                }
                            );
                        },
                        function (errorPayload) {
                        },
                        function (result) {
                            console.log("progress: ", result);
                            scope.files[0].progress = (result.progress / 2).toFixed(1);
                        }
                    );
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upVideo', ['FileService', '$timeout',
    function (fileService, $timeout) {
        return {
            require: '^masonry',
            restrict: 'E',
            replace: true,
            scope: {
                file: '=',
                overlay: '='
            },
            templateUrl: 'assets/templates/up-video.html',
            link: function (scope, element, attrs, masonry) {
                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-video - file changed: ", file);
                    }
                });

                $timeout(function () {
                    $timeout(function () {
                        console.log("up-video - loaded: ");
                        // This code will run after
                        // templateUrl has been loaded, cloned
                        // and transformed by directives.
                        // and properly rendered by the browser
                        var flowplr = element.find(".player");
                        var api = flowplr.flowplayer();
                        api.bind("ready", function(e, api) {
                            console.log("up-video > flowplayer ready", scope.file);
                            masonry.reload();
                        });
                    }, 0);
                }, 0);
            }
        }
    }
]);


angular.module('sproutupApp').directive('upVideoSimple', ['FileService', '$timeout',
    function (fileService, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                file: '=',
                overlay: '='
            },
            templateUrl: 'assets/templates/up-video-simple.html',
            link: function (scope, element, attrs) {
                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-video - file changed: ", file);
                    }
                });

                $timeout(function () {
                    $timeout(function () {
                        console.log("up-video-simple - loaded: ");
                        var flowplr = element.find(".player");
                        var api = flowplr.flowplayer();
                    }, 0);
                }, 0);
            }
        }
    }
]);


angular.module('sproutupApp').directive('upPhoto', ['FileService',
    function (fileService) {
        return {
            restrict: 'E',
            replace: true,
            require: '^masonry',
            scope: {
                file: '='
            },
            templateUrl: 'assets/templates/up-photo.html',
            link: function (scope, element, attrs, ctrl) {
                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-photo - file changed: " + scope.file.id);
                        ctrl.reload();
                    }
                });

                // initialize Masonry after all images have loaded
                imagesLoaded( element[0], function() {
                    console.log("image loaded" + scope.file.id);
                    //msnry = new Masonry( container );
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upFiles', ['$compile', 'FileService',
    function ($compile, fileService) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
            },
            templateUrl: 'assets/templates/up-files.html',
            link: function (scope, element, attrs) {
                flowplayer(function (api, root) {

                  api.bind("load", function () {
                    console.log("video loading")
                    // do something when a new video is about to be loaded

                  }).bind("ready", function () {
                    console.log("video ready")
                    // do something when a video is loaded and ready to play

                  });

                });

                // listen for the event in the relevant $scope
                scope.$on('fileUploadEvent', function (event, args) {
                    console.log('on fileUploadEvent - type: ' + args.data.type);
                    loadFiles();
                    //var elem = $compile( "<up-photo file='file'></up-photo>" )( scope );
                    //element.find('.masonry').prepend(elem);

                    //scope.files.unshift(args.data);
                });

                attrs.$observe('refId', function (refId) {
                    if (refId) {
                        console.log("up-files - ref-id changed: " + refId);
                        loadFiles();
                    }
                });

                function loadFiles() {
                    var promise = fileService.getAllFiles(attrs.refId, attrs.refType);

                    promise.then(function(data) {
                        console.log('up-files received data size: ' + data.length);
                        scope.files = data;
                    }, function(reason) {
                        console.log('up-files failed: ' + reason);
                    }, function(update) {
                        console.log('up-files got notification: ' + update);
                    });
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProfilePhotos', ['FileService',
    function (fileService) {
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                user: "="
            },
            link: function (scope, element, attrs) {
            },
            templateUrl: 'assets/templates/up-profile-photos.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileVideos', ['FileService',
    function (fileService) {
        return {
            restrict: 'EA',
            replace: true,
            scope: true,
            link: function (scope, element, attrs) {
                scope.fileService = fileService;
            },
            templateUrl: 'assets/templates/up-profile-videos.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileInfo', ['AuthService',
    function (authService) {
        return {
            restrict: 'E',
            scope: {
                user: "="
            },
            link: function (scope, element, attrs) {
            },
            templateUrl: 'assets/templates/up-profile-info.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileMenu', ['AuthService','$filter','$state',
    function (authService, $filter, $state) {
        return {
            restrict: 'E',
            scope: {
                user: "="
            },
            link: function (scope, element, attrs) {
                scope.menu = {
                    photos: 0,
                    videos: 0,
                    products: 0
                };

                scope.$watch('user', function(newValue, oldValue) {
                    console.log("profile : watch user: ", scope.user );
                    // get the number of products on this user from the user profile
                    if(scope.user && scope.user.company){
                        scope.menu.products = scope.user.company.products.length;
                    }
                    else{
                        scope.menu.products = 0;
                    }

                    if(scope.user && scope.user.files){
                        scope.menu.photos = $filter("filter")(scope.user.files, {type:"image"}).length;
                        scope.menu.videos = $filter("filter")(scope.user.files, {type:"video"}).length;
                    }
                    else{
                        scope.menu.photos = 0;
                        scope.menu.videos = 0;
                    }
                });
            },
            templateUrl: 'assets/templates/up-profile-menu.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfile', ['$filter', '$log', 'FileService',
    function ($filter, $log, fileService) {
        return {
            restrict: 'EA',
            scope: {
            },
            controller: function($scope) {
                fileService.getAllUserFiles().then(
                    function(data){
                        $log.debug("up-profile - files loaded");
                    },
                    function(error){
                    }
                );
            }
        }
    }
]);

angular.module('sproutupApp').directive('follow', ['FollowService',
    function (FollowService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var isFollowing = false;

            function changeButtonToFollowing() {
                isFollowing = true;
                element.html('<i class="fa fa-check"></i>Following');
                element.addClass("btn-following");
                element.removeClass("btn-outline");
            }

            function changeButtonToFollow() {
                isFollowing = false;
                element.html('Follow');
                element.removeClass("btn-following");
                element.addClass("btn-outline");
            }

            attrs.$observe('refId', function(refId) {
                if (refId) {
                    console.log("refId observe: " + refId);
                    FollowService.isFollowing(refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollowing();
                        },
                        function(errorPayload){
                            changeButtonToFollow();
                        }
                    );
                }
            });

            element.on('click', function () {
                console.log("refId: "+ attrs.refId);
                if(!isFollowing){
                    FollowService.follow(attrs.refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollowing();
                        },
                        function(errorPayload){
                            if(errorPayload="forbidden"){
                                scope.login('sm');
                            }
                            changeButtonToFollow();
                        }
                    );
                }
                else{
                    FollowService.unfollow(attrs.refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollow();
                        },
                        function(errorPayload){
                            changeButtonToFollowing();
                        }
                    );
                }
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upTrial', ['ProductTrialService', 'AuthService',
    function (trialService, authService) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var isFollowing = false;

                function changeButtonToFollowing() {
                    isFollowing = true;
                    element.html('<i class="fa fa-check"></i>Product Trial Requested');
                    element.addClass("btn-following");
                    element.removeClass("btn-outline");
                    element.addClass("disabled");
                }

                function changeButtonToFollow() {
                    isFollowing = false;
                    element.html('Request Product Trial');
                    element.removeClass("btn-following");
                    element.addClass("btn-outline");
                }

                attrs.$observe('refId', function(refId) {
                    if (refId) {
                        console.log("refId observe: " + refId);
                        trialService.didSignUp(refId, attrs.refType)
                            .then(
                            function(payload){
                                changeButtonToFollowing();
                            },
                            function(errorPayload){
                                changeButtonToFollow();
                            }
                        );
                    }
                });

                element.on('click', function () {
                    console.log("refId: "+ attrs.refId);
                    if(authService.isLoggedIn()){
                        scope.trial('sm', {"product_id": attrs.refId, "isLoggedIn": true}).then(
                            function(){
                                console.log("trial success");
                                changeButtonToFollowing();
                            },
                            function(){
                                console.log("trial error");
                            }
                        );
                    }
                    else {
                        scope.trial('sm', {"product_id": attrs.refId, "isLoggedIn": false});
                    }
                });
            }
        };
    }
]);

angular.module('sproutupApp').directive('avatar', function () {
    return {
        restrict: 'E',
        template: '<img ng-src="{{user.avatarUrl}}">',
        link: function (scope, element, attrs) {
            attrs.$observe('class', function(value) {
                if (value) {
                    console.log("value: " + value);
                    element.addClass(value);
                }
            });

            //console.log(attrs.class);
            //element.addClass(attrs.class);
        }
    };
});

angular.module('sproutupApp').directive('commentlink', function () {
    return {
        restrict: 'E',
        templateUrl: 'assets/templates/comment-add-link.html'
    };
});

angular.module('sproutupApp').directive('subjectPresent', ['$parse', 'AuthService', '$state',
    function ($parse, authService, $state) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var onPresent = $parse(attrs.subjectPresent);
            var onLogin = $parse(attrs.login);

            element.on('click', function () {
                if(authService.m.isLoggedIn){
                    console.log(attrs.subjectPresent);

                    // The event originated outside of angular,
                    // We need to call $apply
                    scope.$apply(function () {
                        onPresent(scope);
                    });
                }
                else{
                    console.log(attrs.login);
                    scope.$apply(function () {
                        onLogin(scope);
                    });
                }
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upToptags', ['TagsService', '$timeout',
    function (tagService, $timeout) {
    return {
        templateUrl: 'assets/templates/up-toptags.html',
        scope: {
            productId: "=",
            category: "="
        },
        link: function (scope, element, attrs) {
            attrs.$observe('productId', function (productId) {
                console.log("up-top-tags observe");
                if(scope.productId === undefined){
                    console.log("up-top-tags : product id type = ", typeof scope.productId);
                    // wait and try again
                    $timeout(
                        function(){
                            console.log("up-top-tags timeout : product id type = ", typeof scope.productId);
                            refresh();
                        },
                        1000,
                        true,
                        scope
                    );
                }
                else{
                    refresh();
                }
            });

            var refresh = function(){
                tagService.getPopularPostTags(scope.productId, scope.category).then(
                    function(data){
                        scope.tags = data;
                    },
                    function(error){
                    }
                );

                switch(scope.category) {
                    case 0:
                        scope.cat = "suggestions";
                        break;
                    case 1:
                        scope.cat = "questions";
                        break;
                    case 2:
                        scope.cat = "compliments";
                        break;
                    default:
                        scope.cat = "default";
                }
            }
        }
    };
}]);

/*
    Product List
 */
angular.module('sproutupApp').directive('upProductList', [ 'ProductServicex',
    function (productService) {
        return {
            restrict: 'A',
            transclude:true,
            scope: {
            },
            link: function (scope, element, attrs, ctrl, transclude) {
                // get the product list
                scope.query = "";
                productService.query().then(
                    function(result){
                        scope.products = result;
                        for (var i = 0; i < scope.products.length; i++) {
                            scope.products[i].random = Math.random();
                        }
                    }
                );


                // add the directive scope to the transcluded content
                transclude(scope, function(clone, scope) {
                    element.append(clone);
                });
            }
        };
    }]);

angular.module('sproutupApp').directive('upProductCreate', [ 'ProductService', 'CompanyService', '$state', '$rootScope', 'AuthService',
    function (productService, companyService, $state, $rootScope, authService) {
        return {
            restrict: 'A',
            transclude:true,
            scope: {
                user: "="
            },
            link: function (scope, element, attrs, ctrl, transclude) {
                attrs.$observe('user', function (user) {
                    console.log("product add: user observe");
                    if(scope.user === undefined){
                    }
                    else{
                        if(scope.user.company != null){
                            console.log("product add: found company", scope.user.company.name);
                            scope.product = {
                                company : {
                                    id: scope.user.company.id,
                                    companyName: scope.user.company.name
                                    }
                                };
                        }
                        else{
                            console.log("product add: didn't find company");
                        }
                    }
                });

                scope.save = function(){
                    console.log("product save", scope.product);
                    productService.save(scope.product,function(newProduct){
                        console.log("product save success", newProduct);
                        $rootScope.$broadcast('alert:success', {
                            message: 'Thanks for adding a product! We will review quickly and then will make it live.'
                        });
                        authService.user().then(
                            function(result){
                                $state.go('dashboard.products.info', {slug: newProduct.slug} );
                            }
                        )
                    });
                }

                scope.cancel = function(){
                    console.log("product cancel");
                    $state.go('home');
                }

                // add the directive scope to the transcluded content
                transclude(scope, function(clone, scope) {
                    element.append(clone);
                    console.log("product add: clone");
                });
            }
        };
    }]);

angular.module('sproutupApp').directive('upProductItem', [
    function () {
        return {
            templateUrl: 'assets/templates/up-product-item.html',
            //require: '^upProductList',
            scope: {
                product: "="
            },
            link: function (scope, element, attrs) {
                attrs.$observe('product', function (producty) {
                });

                scope.getActiveRequests = function(product) {
                    if (typeof product.trials !== 'undefined') {
                        return product.trials.filter(function (element) {
                            return element.status >= 0;
                        });
                    }
                    else return [];
                }
            }
        };
    }]);

angular.module('sproutupApp').directive('httpPrefix', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            httpPrefix: "@"
        },
        link: function(scope, element, attrs, controller) {
            function ensureHttpPrefix(value) {
                // If input starts with a @ then strip and prepend twitter url
                if (value) console.log("test:", scope.httpPrefix, value.indexOf('@'));
                if(value && value.indexOf('@') === 0) {
                    console.log("found @");
                    controller.$setViewValue('http://' + scope.httpPrefix + value.slice(1) );
                    controller.$render();
                    return 'http://' + scope.httpPrefix + value.slice(1);
                }
                // Need to add prefix if we don't have http:// prefix already AND we don't have part of it
                else if(value && !/^(https?):\/\//i.test(value)
                   && 'http://'.indexOf(value) === -1 && 'https://'.indexOf(value) === -1) {
                    console.log("found missing prefix");
                    controller.$setViewValue('http://' + scope.httpPrefix + value);
                    controller.$render();
                    return 'http://' + scope.httpPrefix + value;
                }
                else
                    return value;
            }
            controller.$formatters.push(ensureHttpPrefix);
            controller.$parsers.push(ensureHttpPrefix);
        }
    };
});

angular.module('sproutupApp').directive('upWizard', [ '$state', "$rootScope", 'AuthService', 'UserServiceOld',
    function ($state, $rootScope, authService, userService) {
        return {
            restrict: 'A',
            transclude:true,
            scope: {
            },
            link: function (scope, element, attrs, ctrl, transclude) {

                var currentuser = authService.currentUser();

                var copyUser = function(copy, orig){
                    copy.id = orig.id;
                    copy.email = orig.email;
                    copy.firstname = orig.firstname;
                    copy.lastname = orig.lastname;
                    copy.name = orig.name;
                    copy.nickname = orig.nickname;
                    copy.description = orig.description;
                };

                var updated = {
                    "id" : currentuser.id,
                    "email" : currentuser.email,
                    "firstname" : currentuser.firstname,
                    "lastname" : currentuser.lastname,
                    "name" : currentuser.name,
                    "nickname" : currentuser.nickname,
                    "description" : currentuser.description,
                };

                scope.user = updated;

                scope.save = function(){
                    console.log("profile save", scope.user);
                    userService.update(scope.user).then(
                        function(data){
                            console.log("up-profile-edit > update success");
                            currentuser.description = data.description;
                            currentuser.name = data.name;
                            currentuser.nickname = data.nickname;
                            currentuser.firstname = data.firstname;
                            currentuser.lastname = data.lastname;
                            $rootScope.$broadcast('alert:success', {
                                message: 'Your profile is saved'
                            });
                            scope.basicinfoform.$setPristine();
                        },
                        function(error){
                            console.log("up-profile-edit > update failed");
                        }
                    )
                }

                scope.cancel = function(){
                    console.log("profile cancel");
                    $state.go('home');
                }

                // add the directive scope to the transcluded content
                transclude(scope, function(clone, scope) {
                    element.append(clone);
                    console.log("profile add: clone");
                });
            }
        };
    }]);



angular.module('sproutupApp').directive('upUserProfileForm', [ '$state', "$rootScope", 'AuthService', 'UserServiceOld',
    function ($state, $rootScope, authService, userService) {
        return {
            restrict: 'A',
            transclude:true,
            scope: {
            },
            link: function (scope, element, attrs, ctrl, transclude) {

                var currentuser = authService.currentUser();

                var copyUser = function(copy, orig){
                    copy.id = orig.id;
                    copy.email = orig.email;
                    copy.firstname = orig.firstname;
                    copy.lastname = orig.lastname;
                    copy.name = orig.name;
                    copy.nickname = orig.nickname;
                    copy.description = orig.description;
                };

                var updated = {
                    "id" : currentuser.id,
                    "email" : currentuser.email,
                    "firstname" : currentuser.firstname,
                    "lastname" : currentuser.lastname,
                    "name" : currentuser.name,
                    "nickname" : currentuser.nickname,
                    "description" : currentuser.description,
                };

                scope.user = updated;

                scope.save = function(){
                    console.log("profile save", scope.user);
                    userService.update(scope.user).then(
                        function(data){
                            console.log("up-profile-edit > update success");
                            currentuser.description = data.description;
                            currentuser.name = data.name;
                            currentuser.nickname = data.nickname;
                            currentuser.firstname = data.firstname;
                            currentuser.lastname = data.lastname;
                            $rootScope.$broadcast('alert:success', {
                                message: 'Your profile is saved'
                            });
                            scope.basicinfoform.$setPristine();
                        },
                        function(error){
                            console.log("up-profile-edit > update failed");
                        }
                    )
                }

                scope.cancel = function(){
                    console.log("profile cancel");
                    $state.go('home');
                }

                // add the directive scope to the transcluded content
                transclude(scope, function(clone, scope) {
                    element.append(clone);
                    console.log("profile add: clone");
                });
            }
        };
    }]);


angular.module('sproutupApp').directive('navbar', [ 'AuthService', '$rootScope',
    function (authService, $rootScope) {
    return {
        templateUrl: '/assets/templates/navbar.html',
        //template: '<div>{{auth.user.name}} {{auth.isLoggedIn}}</div>',
        controller: 'AuthCtrl',
        controllerAs: 'auth',
        bindToController: true,
//        scope: {
            //isLoggedIn: '='
//        },
        link: function (scope, element, attrs) {
        }
    };
}]);

angular.module('sproutupApp').directive('navbarInfluencer', [ 'AuthService', '$rootScope',
    function (authService, $rootScope) {
    return {
        templateUrl: '/assets/templates/navbar-influencer.html',
        scope: true,
        link: function (scope, element, attrs) {
            scope.user = authService.currentUser();
            scope.isLoggedIn = authService.isLoggedIn();
            scope.$on('auth:status', function (event, args) {
                console.log('event received auth:state ');
                scope.user = authService.currentUser();
                scope.isLoggedIn = authService.isLoggedIn();
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upFbShare', [ '$location', '$window', function ($location, $window) {
    return {
        template: '<a class="post-actions--item" target="_blank" ng-href="{{url}}"><i class="fa fa-facebook-square"></i>Share on Facebook</a>',
        scope: {
            anchor: "="
        },
        link: function (scope, element, attrs) {
            attrs.$observe('anchor', function(anc) {
                scope.url = "http://www.facebook.com/share.php?u=" + encodeURIComponent($location.absUrl() + "#" + scope.anchor);
            });

            //element.on('click', function () {
            //    $window.open(encodeURIComponent(scope.url));
            //});
        }
    };
}]);



angular.module('sproutupApp').directive('upAccessLevel', ['AuthService', '$log',
    function(auth, $log) {
        return {
            restrict: 'A',
            scope: true,
            link: function(scope, element, attrs) {
                var prevDisp = element.css('display')
                    , userRole
                    , accessLevel;

                scope.user = auth.currentUser();
                scope.$watch('user', function(user) {
                    $log.debug("up-access-level - watch - user - " + user.role);
                    if(user.role)
                        userRole = user.role;
                    updateCSS();
                }, true);

                attrs.$observe('upAccessLevel', function(al) {
                    $log.debug("up-access-level - watch - accesslevel - " + al);
                    //if(al) accessLevel = $scope.$eval(al);
                    if(al) accessLevel = al;
                    updateCSS();
                });

                function updateCSS() {
                    if(userRole && accessLevel) {
                        $log.debug("up-access-level - update css");
                        if(!auth.authorize(accessLevel, userRole))
                            element.css('display', 'none');
                        else
                            element.css('display', prevDisp);
                    }
                }
            }
        };
    }
]);
