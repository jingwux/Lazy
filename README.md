# Lazy

为 Spring Converter 的创建提供快捷方式

## 功能

- 根据 Pojo 类快速创建一个 Spring Converter  [new -> Lazy Converter]
- 根据 convert 方法跳转到对应的 Converter 类，与以下 ConverterUtils 结合使用  [Alt + L] [右键 Find Pojo Converter]


```java

@Configuration
public class ApplicationConfiguration {

    @Autowired
    private ConversionService conversionService;

    @PostConstruct
    public void init() {
        ConverterUtils.setConversionService(conversionService);
    }

}


public class ConverterUtils {
    private static ConversionService conversionService;

    public static void setConversionService(ConversionService conversionService) {
        ConverterUtils.conversionService = conversionService;
    }

    public static <FROM, TO> TO convert(FROM from, Class<TO> toClazz) {
        if (conversionService.canConvert(from.getClass(), toClazz)) {
            return conversionService.convert(from, toClazz);
        } else {
            try {
                TO to = toClazz.newInstance();
                BeanUtils.copyProperties(from, to);
                return to;
            } catch (Exception e) {
                // TODO::
                throw new RuntimeException(e);
            }
        }
    }

    public static <FROM, TO> List<TO> convertAll(List<FROM> fromList, Class<TO> toCls) {
        if (fromList == null || fromList.size() == 0) {
            return new ArrayList<>();
        }

        return fromList
                .stream()
                .map(from -> convert(from, toCls)).collect(Collectors.toList());
    }
}
```

## 安装

- IDEA 插件市场直接安装
- https://plugins.jetbrains.com/plugin/14381-lazy 
- https://github.com/yuanqingx/Lazy/releases

## 计划

考虑集成更多的功能